package com.leiyu.distributedserver.socketserver.service.main;

import com.leiyu.distributedserver.api.HelloWorldService;
import com.leiyu.distributedserver.socketserver.service.impl.HelloWorldServiceImpl;
import com.leiyu.distributedserver.socketserver.service.proxy.ProviderReflect;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.socketserver.service.main
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-28
 */
public class RpcProviderMain {
    public static void main(String[] args) throws Exception {
        HelloWorldService helloWorldService = new HelloWorldServiceImpl();
        ProviderReflect.provider(helloWorldService,8083);
    }
}
