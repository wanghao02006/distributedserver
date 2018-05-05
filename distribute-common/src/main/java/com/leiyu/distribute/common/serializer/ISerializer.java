package com.leiyu.distribute.common.serializer;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-05
 */
public interface ISerializer {

    /**
     * 序列化为字节码
     * @param obj
     * @param <T>
     * @return
     */
    public <T> byte[] serialize(T obj);

    /**
     * 字节码反序列化为对象
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T deserialize(byte[] data,Class<T> clazz);
}
