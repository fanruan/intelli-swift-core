package com.fr.swift.base.json;

import com.fr.swift.base.json.annotation.JsonMapper;
import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.base.json.mapper.SwiftBeanMapper;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018-12-04
 */
public class JsonBuilder {
    private static final Map<Class<? extends BeanMapper>, BeanMapper> MAPPERS = new ConcurrentHashMap<Class<? extends BeanMapper>, BeanMapper>();
    private static final Map<Class, BeanMapper> BEAN_MAPPER_MAP = new ConcurrentHashMap<Class, BeanMapper>();
    private static final BeanMapper DEFAULT = new SwiftBeanMapper();

    public static <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        BeanMapper mapper;
        if (!ReflectUtils.isAssignable(reference, Map.class) && reference.isAnnotationPresent(JsonMapper.class)) {
            JsonMapper jsonMapper = reference.getAnnotation(JsonMapper.class);
            mapper = getMapper(jsonMapper);
        } else {
            mapper = calculateMapper(reference);
        }
        if (null == mapper) {
            mapper = DEFAULT;
        }
        return mapper.readValue(jsonString, reference);
    }

    public static String writeJsonString(Object o) throws Exception {
        if (null == o) {
            return Strings.EMPTY;
        }
        Class reference = o.getClass();
        BeanMapper mapper = calculateMapper(reference);
        if (null == mapper) {
            mapper = DEFAULT;
        }
        BEAN_MAPPER_MAP.put(reference, mapper);
        return mapper.writeValueAsString(o);
    }

    private static BeanMapper calculateMapper(Class ref) throws Exception {
        if (BEAN_MAPPER_MAP.containsKey(ref)) {
            return BEAN_MAPPER_MAP.get(ref);
        }
        for (Class anInterface : ref.getInterfaces()) {
            if (anInterface.isAnnotationPresent(JsonMapper.class)) {
                JsonMapper jsonMapper = (JsonMapper) anInterface.getAnnotation(JsonMapper.class);
                BeanMapper mapper = getMapper(jsonMapper);
                BEAN_MAPPER_MAP.put(ref, mapper);
                return mapper;
            }
        }
        Class superClass = ref.getSuperclass();
        if (null == superClass || superClass.equals(Object.class)) {
            return null;
        }
        return calculateMapper(superClass);
    }

    private static BeanMapper getMapper(JsonMapper jsonMapper) throws Exception {
        BeanMapper mapper;
        Class<? extends BeanMapper> mapperClass = jsonMapper.value();
        if (MAPPERS.containsKey(mapperClass)) {
            mapper = MAPPERS.get(mapperClass);
        } else {
            Constructor<? extends BeanMapper> constructor = mapperClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            mapper = constructor.newInstance();
            MAPPERS.put(mapperClass, mapper);
        }
        return mapper;
    }
}
