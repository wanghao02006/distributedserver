package com.leiyu.distribute.core.model;

import java.io.Serializable;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.model
 * @Description: 远程调用响应实体
 * @Author: wanghao30
 * @Creation Date: 2018-06-06
 */
public class RemoteResponse implements Serializable {

    private static final long serialVersionUID = 8121439833287530649L;

    //UUID,唯一标识一次返回值
    private String uniqueKey;

    //客户端指定的服务超时时间
    private long invokeTimeout;

    //接口调用返回的结果对象
    private Object result;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
