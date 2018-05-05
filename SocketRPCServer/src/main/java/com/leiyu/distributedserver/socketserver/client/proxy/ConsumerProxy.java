package com.leiyu.distributedserver.socketserver.client.proxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.socketserver.client.proxy
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-28
 */
public class ConsumerProxy {

    public static <T> T consume(final Class<T> interfaceClass,final String host,final int port){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host,port);
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        output.writeUTF(method.getName());
                        output.writeObject(args);
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result = input.readObject();
                            if(result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        }finally {
                            input.close();
                        }
                    }finally {
                        output.close();
                    }
                }finally {
                    socket.close();
                }
            }
        });
    }
}
