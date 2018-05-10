package com.leiyu.distribute.common.serializer.impl;

import com.leiyu.distribute.common.serializer.ISerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: 基于xstream的序列化
 * @Author: wanghao30
 * @Creation Date: 2018-05-09
 */
public class XmlSerializer implements ISerializer {

    private static final XStream X_STREAM = new XStream(new DomDriver());

    public <T> byte[] serialize(T obj) {
        return X_STREAM.toXML(obj).getBytes();
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        String xml = new String(data);
        return (T) X_STREAM.fromXML(xml);
    }
}
