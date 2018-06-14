package com.leiyu.distribute.core.provider;

import com.google.common.collect.Lists;
import com.leiyu.distribute.common.utils.IPHelper;
import com.leiyu.distribute.core.model.ProviderService;
import com.leiyu.distribute.core.zk.IRegisterCenter4Provider;
import com.leiyu.distribute.core.zk.RegisterCenter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.provider
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-26
 */
public class ProviderFactoryBean implements FactoryBean,InitializingBean {

    //服务接口
    private Class<?> serviceItf;

    //服务实现 对象
    private Object serviceObject;

    //服务端口
    private String serverPort;

    //服务超时时间
    private long timeout;

    //服务代理对象
    private Object serviceProxyObject;

    //服务提供者唯一标识
    private String appKey;

    //服务分组组名
    private String groupName = "default";

    //服务提供者权重，默认为1，范围为1-100
    private int weight = 1;

    //服务端线程数，默认10个线程
    private int workerThreads = 10;

    @Nullable
    public Object getObject() throws Exception {
        return serviceProxyObject;
    }

    @Nullable
    public Class<?> getObjectType() {
        return serviceItf;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        //启动Netty服务端
        NettyServer.getInstance().start(Integer.parseInt(serverPort));

        //注册到zk,元数据注册中心
        List<ProviderService> providerServiceList = buildProviderServiceInfos();
        IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.getInstance();
        registerCenter4Provider.registerProvider(providerServiceList);
    }

    private List<ProviderService> buildProviderServiceInfos() {
        List<ProviderService> providerList = Lists.newArrayList();
        Method[] methods = serviceObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ProviderService providerService = new ProviderService();
            providerService.setServiceItf(serviceItf);
            providerService.setServiceObject(serviceObject);
            providerService.setServerIp(IPHelper.localIp());
            providerService.setServerPort(Integer.parseInt(serverPort));
            providerService.setTimeout(timeout);
            providerService.setServiceMethod(method);
            providerService.setWeight(weight);
            providerService.setWorkerThreads(workerThreads);
            providerService.setAppKey(appKey);
            providerService.setGroupName(groupName);
            providerList.add(providerService);
        }
        return providerList;
    }

    public Class<?> getServiceItf() {
        return serviceItf;
    }

    public void setServiceItf(Class<?> serviceItf) {
        this.serviceItf = serviceItf;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Object getServiceProxyObject() {
        return serviceProxyObject;
    }

    public void setServiceProxyObject(Object serviceProxyObject) {
        this.serviceProxyObject = serviceProxyObject;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }
}
