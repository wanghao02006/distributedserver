package com.leiyu.distribute.common.serializer.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.enums
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-19
 */
public enum SerializeType {

    DefaultJavaSerializer("DefaultJavaSerializer"),
    HessianSerializer("HessianSerializer"),
    JsonSerializer("JsonSerializer"),
    XmlSerializer("XmlSerializer"),
    MarshallingSerializer("MarshallingSerializer")
    ;

    private String serializeType;

    private SerializeType(String serializeType){
        this.serializeType = serializeType;
    }

    public static SerializeType queryByType(String serializeType){
        if(StringUtils.isBlank(serializeType)){
            return null;
        }
        for(SerializeType serializeType1 : SerializeType.values()){
            if(StringUtils.equals(serializeType,serializeType1.getSerializeType())){
                return serializeType1;
            }
        }
        return null;
    }

    public String getSerializeType() {
        return serializeType;
    }

}
