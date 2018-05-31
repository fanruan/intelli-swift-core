package com.fr.swift.adaptor.log;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.MappedSuperclass;
import com.fr.third.javax.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/25
 */
class SwiftMetaAdaptor {
    static SwiftMetaData adapt(Class<?> entity) {
        List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>();
        for (Field field : getFields(entity)) {
            SwiftMetaDataColumn columnMeta = new MetaDataColumnBean(
                    field.getAnnotation(Column.class).name(),
                    getSqlType(getClassType(field)));
            columnMetas.add(columnMeta);
        }
        return new SwiftMetaDataBean(Schema.DECISION_LOG, getTableName(entity), columnMetas);
    }

    static List<Field> getFields(Class<?> entity) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> superEntity = entity.getSuperclass();
        if (superEntity != null && superEntity.isAnnotationPresent(MappedSuperclass.class)) {
            fields.addAll(getFields(superEntity));
        }
        for (Field field : entity.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }

    static String getTableName(Class<?> entity) {
        return entity.getAnnotation(Table.class).name();
    }

    static Class<?> getClassType(Field field) {
        if (!field.isAnnotationPresent(Convert.class)) {
            return field.getType();
        }

        Type[] types = field.getAnnotation(Convert.class).converter().getGenericInterfaces();
        for (Type type : types) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType != AttributeConverter.class) {
                continue;
            }
            Type secondType = pt.getActualTypeArguments()[1];
            if (secondType instanceof Class) {
                return (Class<?>) secondType;
            }
        }
        return Crasher.crash("cannot find the second generic parameter type of AttributeConverter");
    }

    static int getSqlType(Class<?> field) {
        if (boolean.class == field || Boolean.class == field) {
            return Types.BOOLEAN;
        }
        if (byte.class == field || Byte.class == field) {
            return Types.TINYINT;
        }
        if (short.class == field || Short.class == field) {
            return Types.SMALLINT;
        }
        if (int.class == field || Integer.class == field) {
            return Types.INTEGER;
        }
        if (long.class == field || Long.class == field) {
            return Types.BIGINT;
        }
        if (float.class == field || Float.class == field) {
            return Types.FLOAT;
        }
        if (double.class == field || Double.class == field) {
            return Types.DOUBLE;
        }
        if (char.class == field || Character.class == field) {
            return Types.CHAR;
        }
        if (String.class == field) {
            return Types.VARCHAR;
        }
        if (Date.class.isAssignableFrom(field)) {
            return Types.DATE;
        }
        return Crasher.crash(String.format("type unsupported: %s", field));
    }

    static int getStoreSqlType(Class<?> field) {
        switch (getSqlType(field)) {
            case Types.BOOLEAN:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                return Types.BIGINT;
            case Types.FLOAT:
            case Types.DOUBLE:
                return Types.DOUBLE;
            case Types.CHAR:
            case Types.VARCHAR:
                return Types.VARCHAR;
            case Types.DATE:
                return Types.DATE;
            default:
                return Crasher.crash(String.format("type unsupported: %s", field));
        }
    }
}