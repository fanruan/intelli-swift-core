package com.fr.swift.mapper;

import com.fr.swift.annotation.mapper.MapperColumn;
import com.fr.swift.annotation.mapper.MapperTransfer;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;
import com.fr.swift.util.exception.LambdaWrapper;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lucifer
 * @date 2020/3/13
 * @description
 * @since swift 1.1
 */
public class SwiftMapperFactory implements MapperFactory {

    private Map<Class, Map<String, Field>> fieldMapperCache = new ConcurrentHashMap<>();

    private static final SwiftMapperFactory INSTANCE = new SwiftMapperFactory();

    public static SwiftMapperFactory get() {
        return INSTANCE;
    }

    @Override
    public <T> List<T> getMapperedList(SwiftResultSet swiftResultSet, Class<T> tClass) throws Exception {
        List<T> beanList = new ArrayList<>();
        while (swiftResultSet.hasNext()) {
            beanList.add(getMappered(swiftResultSet.getNextRow(), swiftResultSet.getMetaData(), tClass));
        }
        return beanList;
    }

    @Override
    public <T> T getMappered(Row row, SwiftMetaData metaData, Class<T> tClass) throws Exception {
        Map<String, Field> fieldMap = fieldMapperCache.computeIfAbsent(tClass, this::getFiledNameMap);
        Constructor<T> constructor = tClass.getConstructor();
        constructor.setAccessible(true);
        T target = constructor.newInstance();

        List<String> fieldNames = metaData.getFieldNames();
        for (int i = 0; i < fieldNames.size(); i++) {
            Object value = row.getValue(i);
            String fieldName = fieldNames.get(i);
            Field targetField = fieldMap.get(fieldName);
            if (targetField.getAnnotation(MapperTransfer.class) != null) {
                MapperTransfer annotation = targetField.getAnnotation(MapperTransfer.class);
                Class<? extends MapperTransferFunction> func = annotation.using();
                value = func.newInstance().transfer(value, annotation.paramValue());
            }
            Optional.ofNullable(value).ifPresent(
                    LambdaWrapper.rethrowConsumer(v -> FieldUtils.writeField(targetField, target,
                            ReflectUtils.parseObject(targetField.getType(), v.toString()), true)));
        }
        return target;
    }

    private Map<String, Field> getFiledNameMap(Class tClass) {
        Map<String, Field> filedNameMap = new HashMap<>();
        for (Field declaredField : tClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            MapperColumn annotation = declaredField.getAnnotation(MapperColumn.class);
            if (annotation == null || annotation.mapperColumn().equals(Strings.EMPTY)) {
                filedNameMap.put(declaredField.getName(), declaredField);
            } else {
                filedNameMap.put(annotation.mapperColumn(), declaredField);
            }
        }
        return filedNameMap;
    }

}
