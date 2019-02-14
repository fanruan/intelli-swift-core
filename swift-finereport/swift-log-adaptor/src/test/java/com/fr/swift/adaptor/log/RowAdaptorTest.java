package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.JpaAdaptorTest.A;
import com.fr.swift.adaptor.log.JpaAdaptorTest.ConvertType;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.JpaAdaptor;
import com.fr.swift.util.function.Function;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Table;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/27
 */
public class RowAdaptorTest {

    @Test
    public void applyRowToLog() throws Exception {
        Row row = new ListBasedRow(Arrays.<Object>asList(10L, 100L, 1000D, -100D, "qwe", 100L, 100L, 0L, 195L));

        Function<Row, A> adapter = new DecisionRowAdaptor<A>(A.class, new SwiftMetaDataBean("A",
                Arrays.<SwiftMetaDataColumn>asList(
                        new MetaDataColumnBean("s", Types.BIGINT),
                        new MetaDataColumnBean("l", Types.BIGINT),
                        new MetaDataColumnBean("d1", Types.DOUBLE),
                        new MetaDataColumnBean("d2", Types.DOUBLE),
                        new MetaDataColumnBean("str", Types.VARCHAR),
                        new MetaDataColumnBean("utilDate", Types.DATE),
                        new MetaDataColumnBean("sqlDate", Types.DATE),
                        new MetaDataColumnBean("b", Types.BIGINT),
                        new MetaDataColumnBean("i", Types.BIGINT))));
        A a = adapter.apply(row);
        assertEquals(9, row.getSize());
        assertEquals(((Number) row.getValue(0)).shortValue(), a.s);
        assertEquals(((Number) row.getValue(1)).longValue(), (long) a.l);
        assertEquals(((Number) row.getValue(2)).doubleValue(), a.d1, 0);
        assertEquals(((Number) row.getValue(3)).doubleValue(), a.d2, 0);
        assertEquals(row.getValue(4), a.str);
        assertEquals(new Date((Long) row.getValue(5)), a.utilDate);
        assertEquals(new java.sql.Date((Long) row.getValue(6)), a.sqlDate);
        assertEquals(((Long) row.getValue(7)) != 0L, a.b);
        // todo 数值不要解包，1.8下编译报错
        assertEquals(row.getValue(8), Long.valueOf(a.i));

        row = new ListBasedRow(Collections.<Object>singletonList(1D));
        Function<Row, ConvertType> adapter1 = new DecisionRowAdaptor<ConvertType>(ConvertType.class, new SwiftMetaDataBean("ConvertType", Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnBean("o", Types.BIGINT))));
        ConvertType convertType = adapter1.apply(row);
        assertEquals(row.getValue(0), convertType.o);
    }

    @Test
    public void applyLogToRow() throws Exception {
        A a = new A();
        Function<A, Row> adapter = new SwiftRowAdaptor<A>(A.class, new SwiftMetaDataBean("A",
                Arrays.<SwiftMetaDataColumn>asList(
                        new MetaDataColumnBean("s", Types.BIGINT),
                        new MetaDataColumnBean("l", Types.BIGINT),
                        new MetaDataColumnBean("d1", Types.DOUBLE),
                        new MetaDataColumnBean("d2", Types.DOUBLE),
                        new MetaDataColumnBean("str", Types.VARCHAR),
                        new MetaDataColumnBean("utilDate", Types.DATE),
                        new MetaDataColumnBean("sqlDate", Types.DATE),
                        new MetaDataColumnBean("b", Types.BIGINT),
                        new MetaDataColumnBean("i", Types.BIGINT))));
        Row row = adapter.apply(a);
        assertEquals(9, row.getSize());
        assertEquals(Long.valueOf(a.s), row.getValue(0));
        assertEquals(a.l, row.getValue(1));
        assertEquals(Double.valueOf(a.d1), row.getValue(2));
        assertEquals(a.d2, row.getValue(3));
        assertEquals(a.str, row.getValue(4));
        assertEquals(Long.valueOf(a.utilDate.getTime()), row.getValue(5));
        assertEquals(Long.valueOf(a.sqlDate.getTime()), row.getValue(6));
        assertEquals(Long.valueOf(a.b ? 1L : 0L), row.getValue(7));
        assertEquals(Long.valueOf(a.i), row.getValue(8));

        ConvertType convertType = new ConvertType();
        convertType.o = new Object();
        Function<ConvertType, Row> adapter1 = new SwiftRowAdaptor<ConvertType>(ConvertType.class, new SwiftMetaDataBean("ConvertType", Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnBean("o", Types.BIGINT))));
        row = adapter1.apply(convertType);
        assertEquals(1, row.getSize());
        assertEquals(Long.valueOf(1L), row.getValue(0));
    }

    @Test
    public void testBug() throws Exception {
        // DEC-5138 【10冒烟】【系统管理】短信-运行监控查不到数据
        @Table(name = "a")
        class A {
            @Column(name = "b")
            boolean b;
        }

        SwiftMetaData metaData = JpaAdaptor.adapt(A.class, SwiftDatabase.DECISION_LOG);
        DecisionRowAdaptor<A> adaptor = new DecisionRowAdaptor<A>(A.class, metaData);
        A a = adaptor.apply(new ListBasedRow(Collections.<Object>singletonList(1L)));
        Assert.assertTrue(a.b);
    }
}