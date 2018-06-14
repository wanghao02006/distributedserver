package com.leiyu.distribute.core.consumer;

import com.google.common.collect.Maps;
import com.leiyu.distribute.core.model.RemoteResponse;
import com.leiyu.distribute.core.model.RemoteResponseWrapper;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.consumer
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-06-08
 */
public class RevokerResponseHolder {

    //服务返回结果集
    private static final Map<String,RemoteResponseWrapper> responseMap = Maps.newConcurrentMap();

    //清除过期返回结果
    private static final ExecutorService removeExpireKeyExecutor = Executors.newSingleThreadExecutor();

    static{
        removeExpireKeyExecutor.execute(new Runnable() {
            public void run() {
                while (true){
                    try {
                        for(Map.Entry<String,RemoteResponseWrapper> entry : responseMap.entrySet()){
                            boolean isExpire = entry.getValue().isExpire();
                            if(isExpire){
                                responseMap.remove(entry.getKey());
                            }
                            TimeUnit.MILLISECONDS.sleep(10);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 初始化返回结果容器,requestUniqueKey唯一标识本次调用
     *
     * @param requestUniqueKey
     */
    public static void initResponseData(String requestUniqueKey) {
        responseMap.put(requestUniqueKey, RemoteResponseWrapper.of());
    }


    /**
     * 将Netty调用异步返回结果放入阻塞队列
     *
     * @param response
     */
    public static void putResultValue(RemoteResponse response) {
        long currentTime = System.currentTimeMillis();
        RemoteResponseWrapper responseWrapper = responseMap.get(response.getUniqueKey());
        responseWrapper.setResponseTime(currentTime);
        responseWrapper.getResponseQueue().add(response);
        responseMap.put(response.getUniqueKey(), responseWrapper);
    }


    /**
     * 从阻塞队列中获取Netty异步返回的结果值
     *
     * @param requestUniqueKey
     * @param timeout
     * @return
     */
    public static RemoteResponse getValue(String requestUniqueKey, long timeout) {
        RemoteResponseWrapper responseWrapper = responseMap.get(requestUniqueKey);
        try {
            return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            responseMap.remove(requestUniqueKey);
        }
    }
}
