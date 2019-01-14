package com.fr.swift.adaptor.log;

import com.fr.swift.util.Crasher;
import com.fr.swift.util.JpaAdaptor;
import com.fr.swift.util.function.UnaryOperator;
import com.fr.third.javax.persistence.AttributeConverter;

import java.sql.Types;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/4/26
 */
class DatumConverters {
    static UnaryOperator<Object> getConverter(Class<?> field) {
        return getConverter(JpaAdaptor.getStoreSqlType(field));
    }

    private static UnaryOperator<Object> getConverter(int sqlType) {
        switch (sqlType) {
            case Types.BIGINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return null;
                        }
                        if (p instanceof Boolean) {
                            return ((Boolean) p) ? 1L : 0L;
                        }
                        return ((Number) p).longValue();
                    }
                };
            case Types.DOUBLE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return p == null ? null : ((Number) p).doubleValue();
                    }
                };
            case Types.VARCHAR:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return p == null ? null : p.toString();
                    }
                };
            case Types.DATE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        return p == null ? null : ((Date) p).getTime();
                    }
                };
            default:
                return Crasher.crash(String.format("no compatible converter for sql type: %d", sqlType));
        }
    }

    static UnaryOperator<Object> getReverseConverter(final Class<?> field) {
        switch (JpaAdaptor.getSqlType(field)) {
            case Types.BIT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return false;
                        }
                        return ((Number) p).longValue() != 0L;
                    }
                };
            case Types.BOOLEAN:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return null;
                        }
                        return ((Number) p).longValue() != 0L;
                    }
                };
            case Types.TINYINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? ((byte) 0) : null;
                        }
                        return ((Number) p).byteValue();
                    }
                };
            case Types.SMALLINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? ((short) 0) : null;
                        }
                        return ((Number) p).shortValue();
                    }
                };
            case Types.INTEGER:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? 0 : null;
                        }
                        return ((Number) p).intValue();
                    }
                };

            case Types.BIGINT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? 0L : null;
                        }
                        return ((Number) p).longValue();
                    }
                };
            case Types.FLOAT:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? 0F : null;
                        }
                        return ((Number) p).floatValue();
                    }
                };
            case Types.DOUBLE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? 0D : null;
                        }
                        return ((Number) p).doubleValue();
                    }
                };
            case Types.CHAR:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return field.isPrimitive() ? '\0' : null;
                        }
                        return p.toString().charAt(0);
                    }
                };
            case Types.DATE:
                return new UnaryOperator<Object>() {
                    @Override
                    public Object apply(Object p) {
                        if (p == null) {
                            return null;
                        }
                        try {
                            return field.getConstructor(long.class).newInstance(((Long) p));
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
            return p == null ? null : baseConverter.apply(converter.convertToDatabaseColumn(p));
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
            return p == null ? null : converter.convertToEntityAttribute(baseConverter.apply(p));
        }
    }
}