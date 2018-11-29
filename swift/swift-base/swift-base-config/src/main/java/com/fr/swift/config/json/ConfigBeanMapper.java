package com.fr.swift.config.json;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigBeanMapper {
    String writeValueAsString(Object o) throws Exception;

    Object readValue(String jsonString, ConfigBeanTypeReference reference) throws Exception;

    <T> T readValue(String jsonString, Class<T> reference) throws Exception;
}
