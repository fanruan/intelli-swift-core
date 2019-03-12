package com.fr.swift.base.json.mapper;

import com.fr.swift.base.json.reader.JsonParser;
import com.fr.swift.base.json.writer.impl.ObjectJsonWriter;
import com.fr.swift.util.ReflectUtils;

import java.util.Map;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SwiftBeanMapper implements BeanMapper {
    public SwiftBeanMapper() {
    }

    @Override
    public String writeValueAsString(Object o) throws Exception {
        return new ObjectJsonWriter().write(o);
    }

    @Override
    public Object readValue(String jsonString, BeanTypeReference reference) throws Exception {
        return null;
    }

    @Override
    public <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        JsonParser reader = new JsonParser(jsonString);
        if (ReflectUtils.isPrimitiveOrWrapper(reference)) {
            return (T) ReflectUtils.parseObject(reference, jsonString);
        }
        if (ReflectUtils.isAssignable(reference, String.class)) {
            return (T) jsonString.replace("\"", "");
        }
        return reader.parse(reference);
    }

    @Override
    public <T> T readValue(Map<String, Object> jsonMap, Class<T> reference) throws Exception {
        JsonParser reader = new JsonParser(jsonMap);
        return reader.parse(reference);
    }
}
