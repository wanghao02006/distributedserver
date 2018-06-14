package com.leiyu.distribute.core.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.leiyu.distribute.common.serializer.NettyDecoderHandler;
import com.leiyu.distribute.common.serializer.NettyEncoderHandler;
import com.leiyu.distribute.common.serializer.enums.SerializeType;
import com.leiyu.distribute.common.utils.PropertyConfigeHelper;
import com.leiyu.distribute.core.model.ProviderService;
import com.leiyu.distribute.core.model.RemoteResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-06-14
 */
public class NettyChannelPoolFactory {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannelPoolFactory.class);

    //服务提供者地址与channal对应关系
    private static final Map<InetSocketAddress,ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();

    //阻塞队列长度
    private static final int channelConnectSize = PropertyConfigeHelper.getChannelConnectSize();

    private static final SerializeType SERIALIZE_TYPE = PropertyConfigeHelper.getSerializeType();

    //服务提供者列表
    private List<ProviderService> serviceMetaDataList = Lists.newArrayList();

    private NettyChannelPoolFactory(){

    }

    static final class InnerClass{
        static final NettyChannelPoolFactory NETTY_CHANNEL_POOL_FACTORY = new NettyChannelPoolFactory();
    }

    public static NettyChannelPoolFactory getInstance(){
        return InnerClass.NETTY_CHANNEL_POOL_FACTORY;
    }

    /**
     * 初始化Netty channel 连接队列map
     * @param providerMap
     */
    public void initChannelPoolFactory(Map<String,List<ProviderService>> providerMap){
        Collection<List<ProviderService>> collectionServiceMetaDataList = providerMap.values();

        for(List<ProviderService> serviceMetaDataModels : collectionServiceMetaDataList){
            if(CollectionUtils.isEmpty(serviceMetaDataModels)){
                continue;
            }

            serviceMetaDataList.addAll(serviceMetaDataModels);
        }

        Set<InetSocketAddress> socketAddressSet = Sets.newHashSet();
        for(ProviderService providerService : serviceMetaDataList){
            String serviceIp = providerService.getServerIp();
            int servicePort = providerService.getServerPort();
            InetSocketAddress socketAddress = new InetSocketAddress(serviceIp,servicePort);
            socketAddressSet.add(socketAddress);
        }

        for(InetSocketAddress socketAddress : socketAddressSet){
            try {
                int realChannelConnectSize = 0;
                while (realChannelConnectSize < channelConnectSize){
                    Channel channel = null;
                    while (null == channel){
                        channel = registerChannel(socketAddress);
                    }
                    realChannelConnectSize++;

                    ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
                    if(null == channelArrayBlockingQueue){
                        channelArrayBlockingQueue = new ArrayBlockingQueue<Channel>(channelConnectSize);
                        channelPoolMap.put(socketAddress,channelArrayBlockingQueue);
                    }
                    channelArrayBlockingQueue.offer(channel);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据服务提供者地址获取对应的Netty Channel阻塞队列
     *
     * @param socketAddress
     * @return
     */
    public ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress) {
        return channelPoolMap.get(socketAddress);
    }

    /**
     * Channel使用完毕之后,回收到阻塞队列arrayBlockingQueue
     *
     * @param arrayBlockingQueue
     * @param channel
     * @param inetSocketAddress
     */
    public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel, InetSocketAddress inetSocketAddress) {
        if (arrayBlockingQueue == null) {
            return;
        }

        //回收之前先检查channel是否可用,不可用的话,重新注册一个,放入阻塞队列
        if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            if (channel != null) {
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }
            Channel newChannel = null;
            while (newChannel == null) {
                logger.debug("---------register new Channel-------------");
                newChannel = registerChannel(inetSocketAddress);
            }
            arrayBlockingQueue.offer(newChannel);
            return;
        }
        arrayBlockingQueue.offer(channel);
    }

    /**
     * 为服务提供者地址socketAddress注册新的Channel
     *
     * @param socketAddress
     * @return
     */
    protected Channel registerChannel(InetSocketAddress socketAddress) {
        try {
            EventLoopGroup group = new NioEventLoopGroup(10);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.remoteAddress(socketAddress);

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyEncoderHandler(SERIALIZE_TYPE))
                                    .addLast(new NettyDecoderHandler(RemoteResponse.class,SERIALIZE_TYPE))
                                    .addLast(new NettyClientInvokeHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            final Channel newChannel = channelFuture.channel();
            final CountDownLatch connectedLatch = new CountDownLatch(1);

            final List<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        isSuccessHolder.add(Boolean.TRUE);
                    }else {
                        channelFuture.cause().printStackTrace();
                        isSuccessHolder.add(Boolean.FALSE);
                    }
                    connectedLatch.countDown();
                }
            });

            connectedLatch.await();
            if(isSuccessHolder.get(0)){
                return newChannel;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
