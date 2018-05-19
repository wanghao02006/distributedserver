package com.leiyu.distribute.common.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.leiyu.distribute.common.serializer.ISerializer;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-13
 */
public class Json2Serializer implements ISerializer {
    public <T> byte[] serialize(T obj) {
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat).getBytes();
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return (T)JSON.parseObject(new String(data),clazz);
    }
}
