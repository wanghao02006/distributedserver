package com.leiyu.distribute.common.serializer.impl;

import com.leiyu.distribute.common.serializer.ISerializer;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: java原生xml序列化
 * @Author: wanghao30
 * @Creation Date: 2018-05-09
 */
public class Xml2Serializer implements ISerializer {
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        XMLEncoder xe = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            xe = new XMLEncoder(byteArrayOutputStream);
            xe.writeObject(obj);
            xe.close();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if(null != byteArrayOutputStream){
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(data);
            XMLDecoder xd = new XMLDecoder(inputStream);
            Object obj = xd.readObject();
            xd.close();
            return (T) obj;
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
