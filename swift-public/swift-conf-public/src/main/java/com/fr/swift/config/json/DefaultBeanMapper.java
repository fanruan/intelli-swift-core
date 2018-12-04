package com.fr.swift.config.json;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.base.json.mapper.BeanTypeReference;

/**
 * @author yee
 * @date 2018-11-27
 */
public enum DefaultBeanMapper implements BeanMapper {
    //
    INSTANCE;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String writeValueAsString(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @Override
    public Object readValue(String jsonString, BeanTypeReference reference) throws Exception {
        return objectMapper.readValue(jsonString, (TypeReference) reference);
    }

    @Override
    public <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        return objectMapper.readValue(jsonString, reference);
    }
}
