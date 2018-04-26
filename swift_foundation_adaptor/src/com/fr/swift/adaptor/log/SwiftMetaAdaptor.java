//package com.fr.swift.adaptor.log;
//
//import com.fr.swift.source.MetaDataColumn;
//import com.fr.swift.source.SwiftMetaData;
//import com.fr.swift.source.SwiftMetaDataColumn;
//import com.fr.swift.source.SwiftMetaDataImpl;
//import com.fr.swift.util.Crasher;
//import com.fr.third.javax.persistence.AttributeConverter;
//import com.fr.third.javax.persistence.Column;
//import com.fr.third.javax.persistence.Convert;
//import com.fr.third.javax.persistence.Table;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author anchore
// * @date 2018/4/25
// */
//public class SwiftMetaAdaptor {
//    public static SwiftMetaData adapt(Class<?> c) {
//        Table table = c.getAnnotation(Table.class);
//
//        List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>();
//        for (Field field : c.getDeclaredFields()) {
//            if (!field.isAnnotationPresent(Column.class)) {
//                continue;
//            }
//            Column column = field.getAnnotation(Column.class);
//            SwiftMetaDataColumn columnMeta = new MetaDataColumn(column.name(), getStoreType(field));
//            columnMetas.add(columnMeta);
//        }
//        return new SwiftMetaDataImpl(table.name(), columnMetas);
//    }
//
//    private static int getStoreType(Field field) {
//        if (!field.isAnnotationPresent(Convert.class)) {
//            return toSqlType(field.getType());
//        }
//
//        Type[] types = field.getAnnotation(Convert.class).converter().getGenericInterfaces();
//        for (Type type : types) {
//            if (!(type instanceof ParameterizedType)) {
//                continue;
//            }
//            ParameterizedType pt = (ParameterizedType) type;
//            Type rawType = pt.getRawType();
//            if (rawType != AttributeConverter.class) {
//                continue;
//            }
//            Type secondType = pt.getActualTypeArguments()[1];
//            if (secondType instanceof Class) {
//                return toSqlType((Class<?>) secondType);
//            }
//        }
//        return Crasher.crash("cannot find the second generic parameter type of AttributeConverter");
//    }
//
//    private static int toSqlType(Class<?> field) {
//        for (Class<?> c : AS_LONG) {
//            if (c.isAssignableFrom(field)) {
//                return Types.BIGINT;
//            }
//        }
//        for (Class<?> c : AS_DOUBLE) {
//            if (c.isAssignableFrom(field)) {
//                return Types.DOUBLE;
//            }
//        }
//        for (Class<?> c : AS_STRING) {
//            if (c.isAssignableFrom(field)) {
//                return Types.VARCHAR;
//            }
//        }
//        for (Class<?> c : AS_DATE) {
//            if (c.isAssignableFrom(field)) {
//                return Types.DATE;
//            }
//        }
//        return Crasher.crash(String.format("type unsupported: %s", field));
//    }
//
//    private static final Class<?>[] AS_LONG = {
//            boolean.class, Boolean.class,
//            byte.class, Byte.class,
//            short.class, Short.class,
//            int.class, Integer.class,
//            long.class, Long.class
//    };
//
//    private static final Class<?>[] AS_DOUBLE = {
//            float.class, Float.class,
//            double.class, Double.class
//    };
//
//    private static final Class<?>[] AS_STRING = {
//            char.class, Character.class, String.class
//    };
//
//    private static final Class<?>[] AS_DATE = {
//            Date.class
//    };
//}