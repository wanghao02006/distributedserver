package com.leiyu.distributedserver.rmi.factory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.rmi.factory
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-23
 */
public class CustomerSockerFactory extends RMISocketFactory {
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(host,port);
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        if(port == 0){
            port = 8501;
        }
        System.out.println("the port is: " + port);

        return new ServerSocket(port);
    }
}
