package com.leiyu.distribute.core.provider;

import com.leiyu.distribute.common.serializer.NettyDecoderHandler;
import com.leiyu.distribute.common.serializer.NettyEncoderHandler;
import com.leiyu.distribute.common.serializer.enums.SerializeType;
import com.leiyu.distribute.common.utils.PropertyConfigeHelper;
import com.leiyu.distribute.core.model.RemoteRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.provider
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class NettyServer {

    private Channel channel;
    ////服务端boss线程组
    private EventLoopGroup bossGroup;
    //服务端worker线程组
    private EventLoopGroup workerGroup;
    //序列化类型配置信息
    private SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

    private NettyServer(){

    }

    public static NettyServer getInstance(){
        return InnerClass.server;
    }

    static class InnerClass{
        static NettyServer server = new NettyServer();
    }

    public void start(final int port){
        synchronized (NettyServer.class){
            if(null != bossGroup && null != workerGroup){
                return;
            }

            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //注册解码器NettyDecoderHandler
                            ch.pipeline().addLast(new NettyDecoderHandler(RemoteRequest.class, serializeType));
                            //注册编码器NettyEncoderHandler
                            ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
                            //注册服务端业务逻辑处理器NettyServerInvokeHandler
                            ch.pipeline().addLast(new NettyServerInvokeHandler());
                        }
                    });
            try {
                channel = serverBootstrap.bind(port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
