package com.fr.swift.base.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author yee
 * @date 2018-12-04
 */
public class JsonBuilder {
    private static final ObjectMapper DEFAULT = new ObjectMapper();

    public static <T> T readValue(Map<String, Object> jsonMap, Class<T> reference) throws Exception {
        return DEFAULT.readValue(DEFAULT.writeValueAsString(jsonMap), reference);
    }

    public static <T> T readValue(String jsonString, TypeReference<T> reference) throws Exception {
        return DEFAULT.readValue(jsonString, reference);
    }

    public static <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        return DEFAULT.readValue(jsonString, reference);
    }

    public static String writeJsonString(Object o) throws Exception {
        return DEFAULT.writeValueAsString(o);
    }

}