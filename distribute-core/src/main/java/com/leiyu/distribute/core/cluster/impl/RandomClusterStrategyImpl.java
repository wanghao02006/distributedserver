package com.leiyu.distribute.core.cluster.impl;

import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.model.ProviderService;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.impl
 * @Description: 负载均衡-随机
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class RandomClusterStrategyImpl implements ClusterStrategy {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        int index = RandomUtils.nextInt(0,providerServices.size());
        return providerServices.get(index);
    }
}
