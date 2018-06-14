package com.leiyu.distribute.core.cluster;

import com.leiyu.distribute.core.model.ProviderService;

import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster
 * @Description: 负载策略
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public interface ClusterStrategy {

    /**
     * 负载均衡算法
     * @param providerServices
     * @return
     */
    public ProviderService select(List<ProviderService> providerServices);
}
