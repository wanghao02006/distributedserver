package com.leiyu.distributedserver.socketserver.service.proxy;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distributedserver.socketserver.service.proxy
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-03-28
 */
public class ProviderReflect {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void provider(final Object service,int port) throws Exception{
        ServerSocket serverSocket = new ServerSocket(port);
        while (true){
            final Socket socket = serverSocket.accept();
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            String methodName = input.readUTF();
                            Object[] arguments = (Object[])input.readObject();
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            try {
                                Object result = MethodUtils.invokeExactMethod(service,methodName,arguments);
                                output.writeObject(result);
                            }catch (Throwable t){
                                output.writeObject(t);
                            }finally {
                                output.close();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            input.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        System.out.println(Integer.highestOneBit((50 - 1) << 1));
    }
}
