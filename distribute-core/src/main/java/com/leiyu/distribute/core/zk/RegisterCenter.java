package com.leiyu.distribute.core.zk;

import com.leiyu.distribute.model.InvokerService;
import com.leiyu.distribute.model.ProviderService;

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

    private RegisterCenter(){

    }

    public static RegisterCenter getInstance(){
        return InstanceInnerClass.registerCenter;
    }

    public void registerProvider(List<ProviderService> serviceMetaData) {

    }

    public Map<String, List<ProviderService>> getProviderServiceMap() {
        return null;
    }

    public void initProviderMap() {

    }

    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
        return null;
    }

    public void registerInvoker(InvokerService invoker) {

    }

    static class InstanceInnerClass{
        public static RegisterCenter registerCenter = new RegisterCenter();
    }
}
