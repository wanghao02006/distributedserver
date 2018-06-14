package com.leiyu.distribute.core.cluster.impl;

import com.google.common.collect.Lists;
import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.model.ProviderService;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.impl
 * @Description: 负载均衡-带权重随机
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class WeightRandomClusterStrategyImpl implements ClusterStrategy {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        List<ProviderService> providers = Lists.newArrayList();
        for(ProviderService providerService : providerServices){
            int weight = providerService.getWeight();
            for(int i = 0 ; i < weight ; i++){
                providers.add(providerService.copy());
            }
        }
        int index = RandomUtils.nextInt(0,providers.size() - 1);
        return providers.get(index);
    }
}
