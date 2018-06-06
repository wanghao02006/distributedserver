package com.leiyu.distribute.core.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leiyu.distribute.common.utils.IPHelper;
import com.leiyu.distribute.common.utils.PropertyConfigeHelper;
import com.leiyu.distribute.model.InvokerService;
import com.leiyu.distribute.model.ProviderService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.zk
 * @Description: 注册中心实现
 * @Author: wanghao30
 * @Creation Date: 2018-05-28
 */
public class RegisterCenter implements IRegisterCenter4Provider,IRegisterCenter4Invoker {

    private static final Map<String,List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();

    private static final Map<String,List<ProviderService>> serviceMetaDataMap4Consume = Maps.newConcurrentMap();

    private static String ZK_SERVICE = PropertyConfigeHelper.getZkService();

    private static int ZK_SESSION_TIME_OUT = PropertyConfigeHelper.getZkSessionTimeout();

    private static int ZK_CONNECTION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();

    private static String ROOT_PATH = "/config_register";

    public static String PROVIDER_TYPE = "provider";
    public static String INVOKER_TYPE = "consumer";

    private static volatile ZkClient zkClient = null;

    private RegisterCenter(){

    }

    /**
     * 单例实例
     */
    static class InstanceInnerClass{
        public static RegisterCenter registerCenter = new RegisterCenter();
    }

    public static RegisterCenter getInstance(){
        return InstanceInnerClass.registerCenter;
    }

    /**
     * 将服务提供者注册到zk对应节点
     * @param serviceMetaData
     */
    public void registerProvider(final List<ProviderService> serviceMetaData) {
        if(CollectionUtils.isEmpty(serviceMetaData)){
            return;
        }

        synchronized (RegisterCenter.class){
            for(ProviderService provider : serviceMetaData){
                String serviceItfKey = provider.getServiceItf().getName();
                List<ProviderService> providers = providerServiceMap.get(serviceItfKey);

                if(null == providers){
                    providers = Lists.newArrayList();
                }

                providers.add(provider);
                providerServiceMap.put(serviceItfKey,providers);
            }

            if(null == zkClient){
                zkClient = new ZkClient(ZK_SERVICE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT,new SerializableSerializer());
            }

            String APP_KEY = serviceMetaData.get(0).getAppKey();
            String ZK_PATH = ROOT_PATH + "/" + APP_KEY;

            boolean exist = zkClient.exists(ZK_PATH);
            if(!exist){//根目录不存在，创建根目录：/config_register/应用名称
                zkClient.createPersistent(ZK_PATH, true);
            }

            for(Map.Entry<String,List<ProviderService>> entry : providerServiceMap.entrySet()){
                //服务分组
                String groupName = entry.getValue().get(0).getGroupName();
                //创建服务提供者
                String serviceNode = entry.getKey();
                String servicePath = ZK_PATH + "/" + groupName + "/" + serviceNode + "/" + PROVIDER_TYPE;
                exist = zkClient.exists(servicePath);
                if (!exist) {//目录不存在，创建目录：/config_register/应用名称/组名/接口名/provider
                    zkClient.createPersistent(servicePath, true);
                }

                //创建当前服务器节点
                int serverPort = entry.getValue().get(0).getServerPort();//服务端口
                int weight = entry.getValue().get(0).getWeight();//服务权重
                int workerThreads = entry.getValue().get(0).getWorkerThreads();//服务工作线程
                String localIp = IPHelper.localIp();
                String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort + "|" + weight + "|" + workerThreads + "|" + groupName;
                exist = zkClient.exists(currentServiceIpNode);
                if (!exist) {
                    //注意,这里创建的是临时节点
                    zkClient.createEphemeral(currentServiceIpNode);
                }

                //监听注册服务的变化,同时更新数据到本地缓存
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        if (currentChilds == null) {
                            currentChilds = Lists.newArrayList();
                        }

                        //存活的服务IP列表
                        List<String> activityServiceIpList = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                            public String apply(String input) {
                                return StringUtils.split(input, "|")[0];
                            }
                        }));
                        refreshActivityService(activityServiceIpList);
                    }
                });
            }
        }
    }

    /**
     * 服务端获取服务提供者信息
     * @return
     */
    public Map<String, List<ProviderService>> getProviderServiceMap() {
        return providerServiceMap;
    }

    /**
     * 消费端初始化服务提供者信息本地缓存
     */
    public void initProviderMap(String remoteAppKey, String groupName) {
        if (CollectionUtils.isEmpty(serviceMetaDataMap4Consume)) {
            serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData(remoteAppKey, groupName));
        }
    }

    /**
     * 消费端获取服务提供者信息
     * @return
     */
    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
        return serviceMetaDataMap4Consume;
    }

    /**
     * 消费端将消费者注册到zk节点下
     * @param invoker
     */
    public void registerInvoker(InvokerService invoker) {
        if (invoker == null) {
            return;
        }

        //连接zk,注册服务
        synchronized (RegisterCenter.class) {

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
            //创建 ZK命名空间/当前部署应用APP命名空间/
            boolean exist = zkClient.exists(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH, true);
            }


            //创建服务消费者节点
            String remoteAppKey = invoker.getRemoteAppKey();
            String groupName = invoker.getGroupName();
            String serviceNode = invoker.getServiceItf().getName();
            String servicePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName + "/" + serviceNode + "/" + INVOKER_TYPE;
            exist = zkClient.exists(servicePath);
            if (!exist) {
                zkClient.createPersistent(servicePath, true);
            }

            //创建当前服务器节点
            String localIp = IPHelper.localIp();
            String currentServiceIpNode = servicePath + "/" + localIp;
            exist = zkClient.exists(currentServiceIpNode);
            if (!exist) {
                //注意,这里创建的是临时节点
                zkClient.createEphemeral(currentServiceIpNode);
            }
        }
    }

    //利用ZK自动刷新当前存活的服务提供者列表数据
    private void refreshActivityService(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
        for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
            String key = entry.getKey();
            List<ProviderService> providerServices = entry.getValue();

            List<ProviderService> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
            if (serviceMetaDataModelList == null) {
                serviceMetaDataModelList = Lists.newArrayList();
            }

            for (ProviderService serviceMetaData : providerServices) {
                if (serviceIpList.contains(serviceMetaData.getServerIp())) {
                    serviceMetaDataModelList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
        }
        providerServiceMap.clear();
        System.out.println("currentServiceMetaDataMap,"+ JSON.toJSONString(currentServiceMetaDataMap));
        providerServiceMap.putAll(currentServiceMetaDataMap);
    }

    private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData(String remoteAppKey, String groupName) {
        final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();
        //连接zk
        synchronized (RegisterCenter.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
        }

        //从ZK获取服务提供者列表
        String providePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName;
        List<String> providerServices = zkClient.getChildren(providePath);

        for (String serviceName : providerServices) {
            String servicePath = providePath + "/" + serviceName + "/" + PROVIDER_TYPE;
            List<String> ipPathList = zkClient.getChildren(servicePath);
            for (String ipPath : ipPathList) {
                String serverIp = StringUtils.split(ipPath, "|")[0];
                String serverPort = StringUtils.split(ipPath, "|")[1];
                int weight = Integer.parseInt(StringUtils.split(ipPath, "|")[2]);
                int workerThreads = Integer.parseInt(StringUtils.split(ipPath, "|")[3]);
                String group = StringUtils.split(ipPath, "|")[4];

                List<ProviderService> providerServiceList = providerServiceMap.get(serviceName);
                if (providerServiceList == null) {
                    providerServiceList = Lists.newArrayList();
                }
                ProviderService providerService = new ProviderService();

                try {
                    providerService.setServiceItf(ClassUtils.getClass(serviceName));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                providerService.setServerIp(serverIp);
                providerService.setServerPort(Integer.parseInt(serverPort));
                providerService.setWeight(weight);
                providerService.setWorkerThreads(workerThreads);
                providerService.setGroupName(group);
                providerServiceList.add(providerService);

                providerServiceMap.put(serviceName, providerServiceList);
            }

            //监听注册服务的变化,同时更新数据到本地缓存
            zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    if (currentChilds == null) {
                        currentChilds = Lists.newArrayList();
                    }
                    currentChilds = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                        public String apply(String input) {
                            return StringUtils.split(input, "|")[0];
                        }
                    }));
                    refreshServiceMetaDataMap(currentChilds);
                }
            });
        }
        return providerServiceMap;
    }

    private void refreshServiceMetaDataMap(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
        for (Map.Entry<String, List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet()) {
            String serviceItfKey = entry.getKey();
            List<ProviderService> serviceList = entry.getValue();

            List<ProviderService> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
            if (providerServiceList == null) {
                providerServiceList = Lists.newArrayList();
            }

            for (ProviderService serviceMetaData : serviceList) {
                if (serviceIpList.contains(serviceMetaData.getServerIp())) {
                    providerServiceList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(serviceItfKey, providerServiceList);
        }

        serviceMetaDataMap4Consume.clear();
        serviceMetaDataMap4Consume.putAll(currentServiceMetaDataMap);
    }
}
