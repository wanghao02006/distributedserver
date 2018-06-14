package com.leiyu.distribute.core.cluster.impl;

import com.google.common.collect.Lists;
import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.model.ProviderService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.impl
 * @Description: 负载均衡-带权重的轮询
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class WeightPollingClusterStrategyImpl implements ClusterStrategy{

    private volatile int index = 0;

    private Lock lock = new ReentrantLock();

    @Override
    public ProviderService select(List<ProviderService> providerServices) {

        ProviderService providerService = null;

        try {
            lock.tryLock(10, TimeUnit.MILLISECONDS);
            List<ProviderService> providers = Lists.newArrayList();
            for(ProviderService provider : providerServices){
                int weight = provider.getWeight();
                for(int i = 0 ; i < weight ; i++){
                    providers.add(provider.copy());
                }
            }
            if(index >= providers.size()){
                index = 0;
            }
            providerService = providers.get(index);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        if(null == providerService){
            providerService = providerServices.get(0);
        }

        return providerService;
    }
}
