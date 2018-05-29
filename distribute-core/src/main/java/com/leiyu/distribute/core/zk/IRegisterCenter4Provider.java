package com.leiyu.distribute.core.zk;

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
public interface IRegisterCenter4Provider {

    /**
     * 将服务提供者注册到zk对应节点
     * @param serviceMetaData
     */
    void registerProvider(final List<ProviderService> serviceMetaData);

    /**
     * 服务端获取服务提供者信息
     * @return
     */
    Map<String, List<ProviderService>> getProviderServiceMap();
}
