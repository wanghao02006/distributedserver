package com.leiyu.distribute.common.serializer.impl;

import com.leiyu.distribute.common.serializer.ISerializer;

import java.io.*;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: java原生序列化与反序列化
 * @Author: wanghao30
 * @Creation Date: 2018-05-08
 */
public class DefaultJavaSerializer implements ISerializer {
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                byteArrayOutputStream.close();
                if(null != objectOutputStream){
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(data);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            T obj = (T)objectInputStream.readObject();
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(null != byteArrayInputStream){
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null != objectInputStream){
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
