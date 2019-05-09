package com.fr.swift.base.json.mapper;

import java.util.Map;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface BeanMapper {
    String writeValueAsString(Object o) throws Exception;

    Object readValue(String jsonString, BeanTypeReference reference) throws Exception;

    <T> T readValue(String jsonString, Class<T> reference) throws Exception;

    <T> T readValue(Map<String, Object> jsonMap, Class<T> reference) throws Exception;

}
