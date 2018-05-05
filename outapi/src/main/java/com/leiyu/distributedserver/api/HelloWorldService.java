package com.leiyu.distributedserver.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.api
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-23
 */
public interface HelloWorldService extends Remote{

    String sayHello(String username) throws RemoteException;
}
