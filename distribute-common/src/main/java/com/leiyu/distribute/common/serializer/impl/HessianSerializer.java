package com.leiyu.distribute.common.serializer.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.leiyu.distribute.common.serializer.ISerializer;

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
public class HessianSerializer implements ISerializer {
    public <T> byte[] serialize(T obj) {
        if(null == obj){
            throw new NullPointerException();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            HessianOutput ho = new HessianOutput(os);
            ho.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if(null == data){
            throw new NullPointerException("data 为空");
        }

        ByteArrayInputStream is = null;
        try{
            is = new ByteArrayInputStream(data);
            HessianInput hi = new HessianInput(is);
            return (T) hi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
