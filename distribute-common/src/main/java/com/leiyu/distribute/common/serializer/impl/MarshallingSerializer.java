package com.leiyu.distribute.common.serializer.impl;

import com.leiyu.distribute.common.serializer.ISerializer;
import org.jboss.marshalling.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-14
 */
public class MarshallingSerializer implements ISerializer {

    private final static MarshallingConfiguration MARSHALLING_CONFIGURATION = new MarshallingConfiguration();

    private final static MarshallerFactory MARSHALLER_FACTORY = Marshalling.getProvidedMarshallerFactory("serial");

    static {
        MARSHALLING_CONFIGURATION.setVersion(5);
    }

    public <T> byte[] serialize(T obj) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final Marshaller marshaller = MARSHALLER_FACTORY.createMarshaller(MARSHALLING_CONFIGURATION);
            marshaller.start(Marshalling.createByteOutput(byteArrayOutputStream));
            marshaller.writeObject(obj);
            marshaller.finish();
        } catch (IOException e) {
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

        return byteArrayOutputStream.toByteArray();
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayInputStream = new ByteArrayInputStream(data);
            final Unmarshaller unmarshaller = MARSHALLER_FACTORY.createUnmarshaller(MARSHALLING_CONFIGURATION);
            unmarshaller.start(Marshalling.createByteInput(byteArrayInputStream));
            Object object = unmarshaller.readObject();
            unmarshaller.finish();
            return (T)object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
