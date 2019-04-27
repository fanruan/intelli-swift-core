package com.fr.swift.base.json.mapper;

import java.util.Map;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface BeanMapper {
    String writeValueAsString(Object o) throws Exception;

    <T> T string2TypeReference(String jsonString, BeanTypeReference<T> reference) throws Exception;

    <T> T string2Object(String jsonString, Class<T> reference) throws Exception;

    <T> T map2Object(Map<String, Object> jsonMap, Class<T> reference) throws Exception;

}
