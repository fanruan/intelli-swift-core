package com.fr.swift.base.json;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.base.json.mapper.BeanTypeReference;
import com.fr.swift.base.json.mapper.SwiftBeanMapper;

import java.util.Map;

/**
 * @author yee
 * @date 2018-12-04
 */
public class JsonBuilder {
    private static BeanMapper DEFAULT;

    static {
        try {
            DEFAULT = (BeanMapper) Class.forName("com.fr.swift.bytebuddy.SwiftBeanMapper").newInstance();
        } catch (Exception e) {
            DEFAULT = new SwiftBeanMapper();
        }
    }

    public static <T> T readValue(Map<String, Object> jsonMap, Class<T> reference) throws Exception {
        return DEFAULT.map2Object(jsonMap, reference);
    }

    public static <T> T readValue(String jsonString, BeanTypeReference<T> reference) throws Exception {
        return DEFAULT.string2TypeReference(jsonString, reference);
    }

    public static <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        return DEFAULT.string2Object(jsonString, reference);
    }

    public static String writeJsonString(Object o) throws Exception {
        return writeJsonString(o, null);
    }

    public static <T> String writeJsonString(T o, BeanTypeReference<T> reference) throws Exception {
        return DEFAULT.writeValueAsString(o, reference);
    }

}
