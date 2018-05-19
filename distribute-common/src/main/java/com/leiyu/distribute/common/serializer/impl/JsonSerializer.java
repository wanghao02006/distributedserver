package com.leiyu.distribute.common.serializer.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.leiyu.distribute.common.serializer.ISerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.serializer.impl
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-13
 */
public class JsonSerializer implements ISerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        SimpleModule module = new SimpleModule("DateTimeModule", Version.unknownVersion());
        module.addSerializer(Date.class,new FDateJsonSerializer());
        module.addDeserializer(Date.class,new FDateJsonDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    public <T> byte[] serialize(T obj) {
        if(null == obj){
            return new byte[0];
        }

        try {
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            return json.getBytes();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {
        String json = new String(data);
        try {
            return (T)OBJECT_MAPPER.readValue(json,clazz);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static class FDateJsonDeserializer extends JsonDeserializer<Date>{

        public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String date = jsonParser.getText();
            if(StringUtils.isEmpty(date)){
                return null;
            }
            if(StringUtils.isNumeric(date)){
                return new Date(Long.valueOf(date));
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(date);
            }catch (Exception e){
                throw new IOException(e);
            }
        }
    }

    static class FDateJsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Date> {

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jsonGenerator.writeString(null == date ? "null" : sdf.format(date));
        }
    }
}
