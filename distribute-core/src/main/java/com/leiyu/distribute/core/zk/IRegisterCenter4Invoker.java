package com.leiyu.distribute.core.zk;

import com.leiyu.distribute.model.InvokerService;
import com.leiyu.distribute.model.ProviderService;

import java.util.List;
import java.util.Map;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.zk
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-28
 */
public interface IRegisterCenter4Invoker {

    /**
     * 消费端初始化服务提供者信息本地缓存
     */
    void initProviderMap();

    /**
     * 消费端获取服务提供者信息
     * @return
     */
    Map<String,List<ProviderService>> getServiceMetaDataMap4Consume();

    /**
     * 消费端将消费者注册到zk节点下
     * @param invoker
     */
    void registerInvoker(final InvokerService invoker);
}
