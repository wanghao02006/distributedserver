package com.leiyu.distribute.common.serializer;

import org.junit.Test;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-19
 */
public class SerializerEngineTest {

    @Test
    public void testSerialize(){
        User user = new User();
        user.setId(1);
        user.setName("test");
        user.setUsername("test.username");
        byte[] data = SerializerEngine.serialize(user,"DefaultJavaSerializer");
        System.out.println(data.length);
        data = SerializerEngine.serialize(user,"HessianSerializer");
        System.out.println(data.length);
        data = SerializerEngine.serialize(user,"JsonSerializer");
        System.out.println(data.length);
        data = SerializerEngine.serialize(user,"MarshallingSerializer");
        System.out.println(data.length);
        data = SerializerEngine.serialize(user,"XmlSerializer");
        System.out.println(data.length);
    }
}
