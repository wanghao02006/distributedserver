package com.leiyu.distributedserver.test;

import com.leiyu.distributedserver.api.HelloWorldService;
import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.test
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-23
 */
public class HelloServiceTest {

    @Test
    public void testSayHello() throws RemoteException, NotBoundException, MalformedURLException {
        HelloWorldService helloWorldService = (HelloWorldService) Naming.lookup("rmi://localhost:8801/helloService");
        helloWorldService.sayHello("wh");
    }
}
