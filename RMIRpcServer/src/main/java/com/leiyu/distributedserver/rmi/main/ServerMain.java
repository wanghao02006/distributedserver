package com.leiyu.distributedserver.rmi.main;

import com.leiyu.distributedserver.api.HelloWorldService;
import com.leiyu.distributedserver.rmi.factory.CustomerSockerFactory;
import com.leiyu.distributedserver.rmi.impl.HelloWorldServiceImpl;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.rmi.main
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-23
 */
public class ServerMain {

    public static void main(String[] args) throws IOException, AlreadyBoundException {
        HelloWorldService helloWorldService = new HelloWorldServiceImpl();
        LocateRegistry.createRegistry(8801);
        RMISocketFactory.setSocketFactory(new CustomerSockerFactory());
        Naming.bind("rmi://localhost:8801/helloService",helloWorldService);
        System.out.println("Server is started now");
    }
}
