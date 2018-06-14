package com.leiyu.distribute.core.zk;

import com.leiyu.distribute.core.model.InvokerService;
import com.leiyu.distribute.core.model.ProviderService;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.zk
 * @Description: 服务接口
 * @Author: wanghao30
 * @Creation Date: 2018-06-06
 */
public interface IRegisterCenter4Governance {

    /**
     * 获取服务提供者与服务消费者列表
     * @param serviceName
     * @param appKey
     * @return
     */
    public Pair<List<ProviderService>, List<InvokerService>> queryProvidersAndInvokers(String serviceName, String appKey);
}
