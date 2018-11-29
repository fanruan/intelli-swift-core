package com.fr.swift.config.json;

import com.fr.third.fasterxml.jackson.core.type.TypeReference;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author yee
 * @date 2018-11-27
 */
public enum FRConfigBeanMapper implements ConfigBeanMapper {
    INSTANCE;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String writeValueAsString(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @Override
    public Object readValue(String jsonString, ConfigBeanTypeReference reference) throws Exception {
        return objectMapper.readValue(jsonString, (TypeReference) reference);
    }

    @Override
    public <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        return objectMapper.readValue(jsonString, reference);
    }
}
