package com.leiyu.distribute.core.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.model
 * @Description: netty异步调用返回结果包装类
 * @Author: wanghao30
 * @Creation Date: 2018-06-06
 */
public class RemoteResponseWrapper {

    //存储返回结果的阻塞队列
    private BlockingQueue<RemoteResponse> responseQueue = new ArrayBlockingQueue<RemoteResponse>(1);

    //结果返回时间
    private long responseTime;

    /**
     * 计算该返回结果是否已经过期
     *
     * @return
     */
    public boolean isExpire() {
        RemoteResponse response = responseQueue.peek();
        if (response == null) {
            return false;
        }

        long timeout = response.getInvokeTimeout();
        if ((System.currentTimeMillis() - responseTime) > timeout) {
            return true;
        }
        return false;
    }

    public static RemoteResponseWrapper of() {
        return new RemoteResponseWrapper();
    }

    public BlockingQueue<RemoteResponse> getResponseQueue() {
        return responseQueue;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
