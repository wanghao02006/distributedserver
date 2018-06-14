package com.leiyu.distribute.core.consumer;

import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.cluster.engine.ClusterEngine;
import com.leiyu.distribute.core.model.ProviderService;
import com.leiyu.distribute.core.model.RemoteRequest;
import com.leiyu.distribute.core.model.RemoteResponse;
import com.leiyu.distribute.core.zk.IRegisterCenter4Consumer;
import com.leiyu.distribute.core.zk.RegisterCenter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: 消费端bean代理工厂
 * @Author: wanghao30
 * @Creation Date: 2018-06-14
 */
public class ConsumerProxyBeanFactory implements InvocationHandler {

    private ExecutorService fixedThreadPool = null;

    //服务接口
    private Class<?> targetInterface;

    //超时时间
    private int consumeTimeout;

    //调用者线程数
    private static int threadWorkerNumber = 10;

    //负载均衡策略
    private String clusterStrategy;

    private volatile static ConsumerProxyBeanFactory instance;

    private ConsumerProxyBeanFactory(Class<?> targetInterface, int consumeTimeout, String clusterStrategy) {
        this.targetInterface = targetInterface;
        this.consumeTimeout = consumeTimeout;
        this.clusterStrategy = clusterStrategy;
    }

    public static ConsumerProxyBeanFactory getInstance(Class<?> targetInterface, int consumeTimeout, String clusterStrategy){
        if(null == instance){
            synchronized (ConsumerProxyBeanFactory.class){
                if(null == instance){
                    instance = new ConsumerProxyBeanFactory(targetInterface,consumeTimeout,clusterStrategy);
                }
            }
        }

        return instance;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String serviceKey = targetInterface.getName();

        IRegisterCenter4Consumer registerCenter4Consumer = RegisterCenter.getInstance();

        List<ProviderService> providerServices = registerCenter4Consumer.getServiceMetaDataMap4Consume().get(serviceKey);

        ClusterStrategy clusterStrategy = ClusterEngine.queryClusterStrategy(this.clusterStrategy);
        ProviderService providerService = clusterStrategy.select(providerServices);
        ProviderService newProvider = providerService.copy();

        newProvider.setServiceMethod(method);
        newProvider.setServiceItf(targetInterface);

        final RemoteRequest remoteRequest = new RemoteRequest();
        remoteRequest.setUniqueKey(UUID.randomUUID() + "-" + Thread.currentThread().getId());
        remoteRequest.setProviderService(providerService);
        remoteRequest.setInvokeTimeout(consumeTimeout);
        remoteRequest.setInvokedMethodName(method.getName());
        remoteRequest.setArgs(args);

        try {
            if(null == fixedThreadPool){
                synchronized (ConsumerProxyBeanFactory.class){
                    if(null == fixedThreadPool){
                        fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
                    }
                }
            }

            String serverIp = remoteRequest.getProviderService().getServerIp();
            int serverPort = remoteRequest.getProviderService().getServerPort();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp,serverPort);

            Future<RemoteResponse> responseFuture = fixedThreadPool.submit(ConsumerServiceCallable.of(inetSocketAddress,remoteRequest));

            RemoteResponse response = responseFuture.get(remoteRequest.getInvokeTimeout(), TimeUnit.MILLISECONDS);
            if(null != response){
                return response.getResult();
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class<?>[]{targetInterface},this);
    }

}
