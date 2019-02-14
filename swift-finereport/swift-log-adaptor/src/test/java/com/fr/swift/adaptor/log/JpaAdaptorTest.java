package com.fr.swift.adaptor.log;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher.CrashException;
import com.fr.swift.util.JpaAdaptor;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.MappedSuperclass;
import com.fr.third.javax.persistence.Table;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Types;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/25
 */
public class JpaAdaptorTest {

    @Test
    public void adapt() throws SwiftMetaDataException {
        SwiftMetaData meta = JpaAdaptor.adapt(A.class);
        assertEquals("A", meta.getTableName());
        assertEquals(9, meta.getColumnCount());
        assertEquals(Types.SMALLINT, meta.getColumn("s").getType());
        assertEquals(Types.BIGINT, meta.getColumn("l").getType());
        assertEquals(Types.DOUBLE, meta.getColumn("d1").getType());
        assertEquals(Types.DOUBLE, meta.getColumn("d2").getType());
        assertEquals(Types.VARCHAR, meta.getColumn("str").getType());
        assertEquals(Types.DATE, meta.getColumn("sqlDate").getType());
        assertEquals(Types.DATE, meta.getColumn("utilDate").getType());
        assertEquals(Types.BIT, meta.getColumn("b").getType());
        assertEquals(Types.INTEGER, meta.getColumn("i").getType());
    }

    @MappedSuperclass
    static class SuperA {
        @Column(name = "i")
        int i = 192;

        Byte by = -99;
    }

    @Table(name = "A")
    static class A extends SuperA {
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

    @Test(expected = CrashException.class)
    public void adaptUnsupportedType() {
        JpaAdaptor.adapt(UnsupportedType.class);
    }

    @Table(name = "UnsupportedType")
    private static class UnsupportedType {
        @Column(name = "bigDecimal")
        BigDecimal bigDecimal;
    }

    @Test
    public void adaptConvertType() throws SwiftMetaDataException {
        SwiftMetaData meta = JpaAdaptor.adapt(ConvertType.class);
        assertEquals("ConvertType", meta.getTableName());
        assertEquals(1, meta.getColumnCount());
        assertEquals(Types.INTEGER, meta.getColumnType(1));
    }

    @Test(expected = RuntimeException.class)
    public void duplicateColumn() {
        JpaAdaptor.adapt(DuplicateColumnEntity.class);
    }

    @Table(name = "DuplicateColumnEntity")
    class DuplicateColumnEntity {
        @Column(name = "s")
        String s;

        @Column(name = "s")
        String s1;
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
        JpaAdaptor.adapt(WrongConvertType.class);
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

    @Test
    public void testBugfix() throws Exception {
        // DEC-5271
        abstract class BaseConv<X, Y> implements AttributeConverter<X, Y> {
        }

        class Conv extends BaseConv<String, Long> {
            @Override
            public Long convertToDatabaseColumn(String s) {
                return null;
            }

            @Override
            public String convertToEntityAttribute(Long aLong) {
                return null;
            }
        }

        @Table(name = "a")
        class A {
            @Convert(converter = Conv.class)
            @Column(name = "s")
            String s;
        }

        SwiftMetaData meta = JpaAdaptor.adapt(A.class);
        assertEquals("a", meta.getTableName());
        assertEquals(1, meta.getColumnCount());
        assertEquals(Types.BIGINT, meta.getColumn("s").getType());
    }
}