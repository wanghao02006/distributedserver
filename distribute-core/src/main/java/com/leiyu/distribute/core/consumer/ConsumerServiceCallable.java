package com.leiyu.distribute.core.consumer;

import com.leiyu.distribute.core.model.RemoteRequest;
import com.leiyu.distribute.core.model.RemoteResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: 请求发起线程
 * @Author: wanghao30
 * @Creation Date: 2018-06-14
 */
public class ConsumerServiceCallable implements Callable<RemoteResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceCallable.class);

    private Channel channel;

    private InetSocketAddress inetSocketAddress;

    private RemoteRequest remoteRequest;

    public ConsumerServiceCallable(InetSocketAddress inetSocketAddress, RemoteRequest remoteRequest) {
        this.inetSocketAddress = inetSocketAddress;
        this.remoteRequest = remoteRequest;
    }

    public static ConsumerServiceCallable of(InetSocketAddress inetSocketAddress, RemoteRequest remoteRequest){
        return new ConsumerServiceCallable(inetSocketAddress,remoteRequest);
    }

    @Override
    public RemoteResponse call() throws Exception {
        RevokerResponseHolder.initResponseData(remoteRequest.getUniqueKey());

        ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.getInstance().acquire(inetSocketAddress);
        try {
            if(null == channel){
                channel = blockingQueue.poll(remoteRequest.getInvokeTimeout(), TimeUnit.MILLISECONDS);
            }

            while (!channel.isOpen() || !channel.isActive() || !channel.isWritable()){
                logger.warn("----------------retry get new channel----------------");
                channel = blockingQueue.poll(remoteRequest.getInvokeTimeout(),TimeUnit.MILLISECONDS);
                if(null == channel){
                    //若队列中没有可用的Channel,则重新注册一个Channel
                    channel = NettyChannelPoolFactory.getInstance().registerChannel(inetSocketAddress);
                }
            }

            ChannelFuture channelFuture = channel.writeAndFlush(remoteRequest);
            channelFuture.syncUninterruptibly();

            //从返回结果容器中获取返回结果,同时设置等待超时时间为invokeTimeout
            long invokeTimeout = remoteRequest.getInvokeTimeout();
            return RevokerResponseHolder.getValue(remoteRequest.getUniqueKey(), invokeTimeout);
        }catch (Exception e) {
            logger.error("service invoke error.", e);
        } finally {
            //本次调用完毕后,将Netty的通道channel重新释放到队列中,以便下次调用复用
            NettyChannelPoolFactory.getInstance().release(blockingQueue, channel, inetSocketAddress);
        }
        return null;
    }
}
