package com.fr.swift.adaptor.log;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.Table;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Types;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/25
 */
public class SwiftMetaAdaptorTest {

    @Test
    public void adapt() throws SwiftMetaDataException {
        SwiftMetaData meta = SwiftMetaAdaptor.adapt(A.class);
        assertEquals("A", meta.getTableName());
        assertEquals(7, meta.getColumnCount());
        assertEquals(Types.BIGINT, meta.getColumnType(1));
        assertEquals(Types.BIGINT, meta.getColumnType(2));
        assertEquals(Types.DOUBLE, meta.getColumnType(3));
        assertEquals(Types.DOUBLE, meta.getColumnType(4));
        assertEquals(Types.VARCHAR, meta.getColumnType(5));
        assertEquals(Types.DATE, meta.getColumnType(6));
        assertEquals(Types.DATE, meta.getColumnType(7));
    }

    @Table(name = "A")
    static class A {
        @Column(name = "d1")
        double d1 = 1;
        @Column(name = "s")
        short s = -1;
        @Column(name = "l")
        Long l = 1L;

        private Object dontRecordThis;

        @Column(name = "d2")
        Double d2 = 1.;
        @Column(name = "str")
        String str = "asd";
        @Column(name = "utilDate")
        java.util.Date utilDate = new java.util.Date();
        @Column(name = "sqlDate")
        java.sql.Date sqlDate = new java.sql.Date(1);
        @Column(name = "b")
        boolean b = true;
    }

    @Test(expected = RuntimeException.class)
    public void adaptUnsupportedType() {
        SwiftMetaAdaptor.adapt(UnsupportedType.class);
    }

    @Table(name = "UnsupportedType")
    private static class UnsupportedType {
        @Column(name = "bigDecimal")
        BigDecimal bigDecimal;
    }

    @Test
    public void adaptConvertType() throws SwiftMetaDataException {
        SwiftMetaData meta = SwiftMetaAdaptor.adapt(ConvertType.class);
        assertEquals("ConvertType", meta.getTableName());
        assertEquals(1, meta.getColumnCount());
        assertEquals(Types.BIGINT, meta.getColumnType(1));
    }

    @Table(name = "ConvertType")
    static class ConvertType {
        @Column(name = "o")
        @Convert(converter = Converter.class)
        Object o;

        static class Converter implements AttributeConverter<Object, Integer> {
            @Override
            public Integer convertToDatabaseColumn(Object o) {
                return 1;
            }

            @Override
            public Object convertToEntityAttribute(Integer integer) {
                return 1D;
            }
        }
    }

    @Test(expected = RuntimeException.class)
    public void adaptWrongConvertType() {
        SwiftMetaAdaptor.adapt(WrongConvertType.class);
    }

    @Table(name = "WrongConvertType")
    private static class WrongConvertType {
        @Column(name = "o")
        @Convert(converter = Z.class)
        Object o;

        static class Z<T> implements AttributeConverter<Object, T> {
            @Override
            public T convertToDatabaseColumn(Object o) {
                return null;
            }

            @Override
            public Object convertToEntityAttribute(T t) {
                return null;
            }
        }
    }

}