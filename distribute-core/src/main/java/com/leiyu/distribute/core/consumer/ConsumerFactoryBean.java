package com.leiyu.distribute.core.consumer;

import com.leiyu.distribute.core.model.InvokerService;
import com.leiyu.distribute.core.model.ProviderService;
import com.leiyu.distribute.core.zk.IRegisterCenter4Consumer;
import com.leiyu.distribute.core.zk.RegisterCenter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: 服务bean入口
 * @Author: wanghao30
 * @Creation Date: 2018-06-14
 */
public class ConsumerFactoryBean implements FactoryBean,InitializingBean{

    //服务接口
    private Class<?> targetInterface;

    //超时时间
    private int timeout;

    //服务bean
    private Object serviceObject;

    //负载均衡策略
    private String clusterStrategy;

    //服务提供者唯一标识
    private String remoteAppKey;

    //服务分组组名
    private String groupName = "default";

    @Nullable
    @Override
    public Object getObject() throws Exception {
        return serviceObject;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return targetInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IRegisterCenter4Consumer registerCenter4Consumer = RegisterCenter.getInstance();

        //初始化服务提供者列表到本地缓存
        registerCenter4Consumer.initProviderMap(remoteAppKey,groupName);
        Map<String,List<ProviderService>> providerMap = registerCenter4Consumer.getServiceMetaDataMap4Consume();
        if(MapUtils.isEmpty(providerMap)){
            throw new RuntimeException("service provider list is empty!");
        }

        NettyChannelPoolFactory.getInstance().initChannelPoolFactory(providerMap);

        //获取服务提供者代理对象
        ConsumerProxyBeanFactory proxyFactory = ConsumerProxyBeanFactory.getInstance(targetInterface, timeout, clusterStrategy);
        this.serviceObject = proxyFactory.getProxy();

        //将消费者信息注册到注册中心
        InvokerService invoker = new InvokerService();
        invoker.setServiceItf(targetInterface);
        invoker.setRemoteAppKey(remoteAppKey);
        invoker.setGroupName(groupName);
        registerCenter4Consumer.registerInvoker(invoker);

    }

    public Class<?> getTargetInterface() {
        return targetInterface;
    }

    public void setTargetInterface(Class<?> targetInterface) {
        this.targetInterface = targetInterface;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public String getClusterStrategy() {
        return clusterStrategy;
    }

    public void setClusterStrategy(String clusterStrategy) {
        this.clusterStrategy = clusterStrategy;
    }

    public String getRemoteAppKey() {
        return remoteAppKey;
    }

    public void setRemoteAppKey(String remoteAppKey) {
        this.remoteAppKey = remoteAppKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
