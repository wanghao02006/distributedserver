package com.leiyu.distribute.core.cluster.engine;

import com.google.common.collect.Maps;
import com.leiyu.distribute.core.cluster.ClusterStrategy;
import com.leiyu.distribute.core.cluster.enums.ClusterStrategyEnum;
import com.leiyu.distribute.core.cluster.impl.*;

import java.util.Map;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.cluster.engine
 * @Description: 负载均衡引擎
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class ClusterEngine {

    private static final Map<ClusterStrategyEnum,ClusterStrategy> clusterStrategyMap = Maps.newConcurrentMap();

    static {
        clusterStrategyMap.put(ClusterStrategyEnum.Random, new RandomClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.WeightRandom, new WeightRandomClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.Polling, new PollingClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.WeightPolling, new WeightPollingClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.Hash, new HashClusterStrategyImpl());
    }

    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
        ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
        if (clusterStrategyEnum == null) {
            //默认选择随机算法
            return new RandomClusterStrategyImpl();
        }

        return clusterStrategyMap.get(clusterStrategyEnum);
    }
}
