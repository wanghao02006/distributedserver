package com.leiyu.distribute.core.cluster.impl;

import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.model.ProviderService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.impl
 * @Description: 负载均衡-轮询
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class PollingClusterStrategyImpl implements ClusterStrategy{

    private volatile int index = 0;

    private Lock lock = new ReentrantLock();


    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        ProviderService providerService = null;
        try {
            lock.tryLock(10, TimeUnit.MILLISECONDS);

            if(index >= providerServices.size()){
                index = 0;
            }
            providerService = providerServices.get(index);
            index++;
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
