package com.leiyu.distribute.core.cluster.impl;

import com.leiyu.distribute.common.utils.IPHelper;
import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.model.ProviderService;

import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.impl
 * @Description: 负载均衡-hash算法
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class HashClusterStrategyImpl implements ClusterStrategy{
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        String localIP = IPHelper.localIp();
        int hashCode = localIP.hashCode();
        int size = providerServices.size();
        return providerServices.get(hashCode % size);
    }
}
