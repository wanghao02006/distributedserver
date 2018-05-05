package com.leiyu.distributedserver.rmi.impl;

import com.leiyu.distributedserver.api.HelloWorldService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.rmi
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-23
 */
public class HelloWorldServiceImpl extends UnicastRemoteObject implements HelloWorldService {

    public HelloWorldServiceImpl() throws RemoteException{
        super();
    }

    public String sayHello(String username) throws RemoteException {
        System.out.println("Hello " + username);
        return "Hello " + username;
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println(System.currentTimeMillis());
        System.out.println(sdf.parse("20180326 07:00:00").getTime());
        System.out.println(sdf.parse("20180325 07:00:00").getTime());
        System.out.println(sdf.parse("20180326 12:00:00").getTime());
    }
}
