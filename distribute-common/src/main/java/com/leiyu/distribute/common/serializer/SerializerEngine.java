package com.leiyu.distribute.common.serializer;

import com.google.common.collect.Maps;
import com.leiyu.distribute.common.serializer.enums.SerializeType;
import com.leiyu.distribute.common.serializer.impl.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-19
 */
public class SerializerEngine {

    public static final Map<SerializeType,ISerializer> SERIALIZER_MAP = Maps.newConcurrentMap();

    static {
        System.out.println("11111111111111111111");
        SERIALIZER_MAP.put(SerializeType.DefaultJavaSerializer,new DefaultJavaSerializer());
        SERIALIZER_MAP.put(SerializeType.HessianSerializer,new HessianSerializer());
        SERIALIZER_MAP.put(SerializeType.JsonSerializer,new JsonSerializer());
        SERIALIZER_MAP.put(SerializeType.MarshallingSerializer,new MarshallingSerializer());
        SERIALIZER_MAP.put(SerializeType.XmlSerializer,new XmlSerializer());
    }

    public static <T> byte[] serialize(T obj,String strSerializeType){

        if(null == obj){
            throw new RuntimeException("error: the serialize data is null!");
        }
        ISerializer iSerializer = getiSerializer(strSerializeType);

        return iSerializer.serialize(obj);
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz, String strSerializeType){

        if(null == data || data.length == 0){
            throw new RuntimeException("error: the deserialize data is null!");
        }
        ISerializer iSerializer = getiSerializer(strSerializeType);
        return iSerializer.deserialize(data,clazz);
    }

    private static ISerializer getiSerializer(String strSerializeType) {
        if(StringUtils.isBlank(strSerializeType)){
            throw new RuntimeException("error: the serialize type can not empty!");
        }

        SerializeType serializeType = SerializeType.queryByType(strSerializeType);
        if (serializeType == null) {
            throw new RuntimeException("error: can not find the serializeType " + strSerializeType);
        }

        ISerializer iSerializer = SERIALIZER_MAP.get(serializeType);
        if (iSerializer == null) {
            throw new RuntimeException("error: can not stand this serializeType");
        }
        return iSerializer;
    }
}
