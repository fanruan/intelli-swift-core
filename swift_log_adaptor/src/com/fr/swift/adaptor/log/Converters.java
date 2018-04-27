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
    static UnaryOperator<Object> getConverter(Class<?> type) {
        return getConverter(SwiftMetaAdaptor.getStoreSqlType(type));
    }

    private static UnaryOperator<Object> getConverter(int sqlType) {
        switch (sqlType) {
            case Types.BIGINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).longValue();
                    }
                };
            case Types.DOUBLE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).doubleValue();
                    }
                };
            case Types.VARCHAR:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return p.toString();
                    }
                };
            case Types.DATE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Date) p).getTime();
                    }
                };
            default:
                return null;
        }
    }

    static UnaryOperator<Object> getReverseConverter(final Class<?> type) {
        switch (SwiftMetaAdaptor.getSqlType(type)) {
            case Types.BOOLEAN:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Long) p) != 0L;
                    }
                };
            case Types.TINYINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).byteValue();
                    }
                };
            case Types.SMALLINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).shortValue();
                    }
                };
            case Types.INTEGER:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).intValue();
                    }
                };
            case Types.FLOAT:

                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).floatValue();
                    }
                };
            case Types.DOUBLE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return ((Number) p).doubleValue();
                    }
                };
            case Types.CHAR:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return p.toString().charAt(0);
                    }
                };
            case Types.DATE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        try {
                            return type.getConstructor(long.class).newInstance(((Long) p));
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };
            default:
                return new EmptyDatumConverter();
        }
    }

    static class EmptyDatumConverter implements UnaryOperator<Object> {
        @Override
        public Object apply(Object p) {
            return p;
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

    static class ReverseDatumConverter implements UnaryOperator<Object> {
        AttributeConverter<Object, Object> converter;

        UnaryOperator<Object> baseConverter;

        ReverseDatumConverter(AttributeConverter<Object, Object> converter, UnaryOperator<Object> baseConverter) {
            this.converter = converter;
            this.baseConverter = baseConverter;
        }

        @Override
        public Object apply(Object p) {
            return converter.convertToEntityAttribute(baseConverter.apply(p));
        }
    }
}