package com.leiyu.distributedserver.socketserver.client.main;

import com.leiyu.distributedserver.api.HelloWorldService;
import com.leiyu.distributedserver.socketserver.client.proxy.ConsumerProxy;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.socketserver.client.main
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-28
 */
public class RpcConsumerMain {

    public static void main(String[] args) throws RemoteException, InterruptedException {
        HelloWorldService service = ConsumerProxy.consume(HelloWorldService.class,"127.0.0.1",8083);
        for(int i = 0 ; i < 1000; i++){
            String hello = service.sayHello("liyebing_" + i);
            System.out.println(hello);

            TimeUnit.SECONDS.sleep(1);
        }
    }
}
