package com.leiyu.distribute.core.provider;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.leiyu.distribute.core.model.ProviderService;
import com.leiyu.distribute.core.model.RemoteRequest;
import com.leiyu.distribute.core.model.RemoteResponse;
import com.leiyu.distribute.core.zk.IRegisterCenter4Provider;
import com.leiyu.distribute.core.zk.RegisterCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.provider
 * @Description: 处理服务端逻辑
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class NettyServerInvokeHandler extends SimpleChannelInboundHandler<RemoteRequest>{

    private static final Logger logger = LoggerFactory.getLogger(NettyServerInvokeHandler.class);

    private static final Map<String,Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    protected void channelRead0(ChannelHandlerContext ctx, RemoteRequest remoteRequest) throws Exception {
        if(ctx.channel().isWritable()){
            ProviderService metaDataModel = remoteRequest.getProviderService();

            long consumerTimeOut = remoteRequest.getInvokeTimeout();
            final String methodName = remoteRequest.getInvokedMethodName();

            String serviceKey = metaDataModel.getServiceItf().getName();

            int workerThread = metaDataModel.getWorkerThreads();
            Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);

            if(null == semaphore){
                synchronized (serviceKeySemaphoreMap) {
                    semaphore = serviceKeySemaphoreMap.get(serviceKey);
                    if(null == semaphore){
                        semaphore = new Semaphore(workerThread);
                        serviceKeySemaphoreMap.put(serviceKey,semaphore);
                    }
                }
            }

            IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.getInstance();

            List<ProviderService> localProviderCaches = registerCenter4Provider.getProviderServiceMap().get(serviceKey);

            Object result = null;
            boolean acquire = false;

            try {
                ProviderService localProviderCache = Collections2.filter(localProviderCaches, new Predicate<ProviderService>() {
                    public boolean apply(ProviderService providerService) {
                        return StringUtils.equals(providerService.getServiceMethod().getName(),methodName);
                    }

                    public boolean test(ProviderService input) {
                        return StringUtils.equals(input.getServiceMethod().getName(),methodName);
                    }
                }).iterator().next();

                Object serviceObject = localProviderCache.getServiceObject();

                Method method = localProviderCache.getServiceMethod();

                acquire = semaphore.tryAcquire(consumerTimeOut, TimeUnit.MILLISECONDS);
                if(acquire){
                    result = method.invoke(serviceObject,remoteRequest.getArgs());
                }
            }catch (Exception e){
                System.out.println(JSON.toJSONString(localProviderCaches) + "  " + methodName+" "+e.getMessage());
                result = e;
            } finally {
                if (acquire) {
                    semaphore.release();
                }
            }

            RemoteResponse response = new RemoteResponse();
            response.setInvokeTimeout(consumerTimeOut);
            response.setUniqueKey(remoteRequest.getUniqueKey());
            response.setResult(result);

            //将服务调用返回对象回写到消费端
            ctx.writeAndFlush(response);

        }else {
            logger.error("------------channel closed!---------------");
        }
    }
}
