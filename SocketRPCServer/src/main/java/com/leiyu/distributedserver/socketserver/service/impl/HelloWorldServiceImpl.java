package com.leiyu.distributedserver.socketserver.service.impl;

import com.leiyu.distributedserver.api.HelloWorldService;

import java.rmi.RemoteException;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.socketserver.service
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-28
 */
public class HelloWorldServiceImpl implements HelloWorldService {
    public String sayHello(String username) throws RemoteException {
        System.out.println("Hello " + username);
        return "Hello " + username;
    }
}
