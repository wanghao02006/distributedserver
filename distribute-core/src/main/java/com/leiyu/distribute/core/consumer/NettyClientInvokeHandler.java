package com.leiyu.distribute.core.consumer;

import com.leiyu.distribute.core.model.RemoteResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: 消费者接收返回handler
 * @Author: wanghao30
 * @Creation Date: 2018-06-08
 */
public class NettyClientInvokeHandler extends SimpleChannelInboundHandler<RemoteResponse> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RemoteResponse remoteResponse) throws Exception {
        RevokerResponseHolder.putResultValue(remoteResponse);
    }
}
