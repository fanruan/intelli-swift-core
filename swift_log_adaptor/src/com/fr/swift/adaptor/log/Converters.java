package com.fr.swift.adaptor.log;

import com.fr.swift.util.function.UnaryOperator;
import com.fr.third.javax.persistence.AttributeConverter;

import java.sql.Types;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/4/26
 */
class Converters {
    static UnaryOperator<Object> getConverter(int sqlType) {
        switch (sqlType) {
            case Types.BIGINT:
                return new AsLongConverter();
            case Types.DOUBLE:
                return new AsDoubleConverter();
            case Types.VARCHAR:
                return new AsStringConverter();
            case Types.DATE:
                return new AsDateConverter();
            default:
                return null;
        }
    }

    static UnaryOperator<Object> getConverter(Class<?> type) {
        for (Class<?> c : SwiftMetaAdaptor.AS_LONG) {
            if (c.isAssignableFrom(type)) {
                return new AsLongConverter();
            }
        }
        for (Class<?> c : SwiftMetaAdaptor.AS_DOUBLE) {
            if (c.isAssignableFrom(type)) {
                return new AsDoubleConverter();
            }
        }
        for (Class<?> c : SwiftMetaAdaptor.AS_STRING) {
            if (c.isAssignableFrom(type)) {
                return new AsStringConverter();
            }
        }
        for (Class<?> c : SwiftMetaAdaptor.AS_DATE) {
            if (c.isAssignableFrom(type)) {
                return new AsDateConverter();
            }
        }
        return null;
    }

    static class AsLongConverter implements UnaryOperator<Object> {
        @Override
        public Object apply(Object p) {
            return ((Number) p).longValue();
        }
    }

    static class AsDoubleConverter implements UnaryOperator<Object> {
        @Override
        public Object apply(Object p) {
            return ((Number) p).doubleValue();
        }
    }

    static class AsStringConverter implements UnaryOperator<Object> {
        @Override
        public Object apply(Object p) {
            return p.toString();
        }
    }

    static class AsDateConverter implements UnaryOperator<Object> {
        @Override
        public Object apply(Object p) {
            return ((Date) p).getTime();
        }
    }

    static class DatumConverter implements UnaryOperator<Object> {
        AttributeConverter<Object, Object> converter;

        UnaryOperator<Object> baseConverter;

        DatumConverter(AttributeConverter<Object, Object> converter, UnaryOperator<Object> baseConverter) {
            this.converter = converter;
            this.baseConverter = baseConverter;
        }

        @Override
        public Object apply(Object p) {
            return baseConverter.apply(converter.convertToDatabaseColumn(p));
        }
    }
}