package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.A;
import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.ConvertType;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.util.function.Function;
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
        Row row = new ListBasedRow(Arrays.<Object>asList(10L, 100L, 1000D, -100D, "qwe", System.currentTimeMillis(), System.currentTimeMillis(), 0L));

        Function<Row, Object> adapter = new DecisionRowAdaptor(A.class, new SwiftMetaDataImpl("A",
                Arrays.<SwiftMetaDataColumn>asList(
                        new MetaDataColumn("s", Types.BIGINT),
                        new MetaDataColumn("l", Types.BIGINT),
                        new MetaDataColumn("d1", Types.DOUBLE),
                        new MetaDataColumn("d2", Types.DOUBLE),
                        new MetaDataColumn("str", Types.VARCHAR),
                        new MetaDataColumn("utilDate", Types.DATE),
                        new MetaDataColumn("sqlDate", Types.DATE),
                        new MetaDataColumn("b", Types.BIGINT))));
        A a = (A) adapter.apply(row);
        assertEquals(((Number) row.getValue(0)).shortValue(), a.s);
        assertEquals(((Number) row.getValue(1)).longValue(), (long) a.l);
        assertEquals(((Number) row.getValue(2)).doubleValue(), a.d1, 0);
        assertEquals(((Number) row.getValue(3)).doubleValue(), a.d2, 0);
        assertEquals(row.getValue(4), a.str);
        assertEquals(new Date((Long) row.getValue(5)), a.utilDate);
        assertEquals(new java.sql.Date((Long) row.getValue(6)), a.sqlDate);
        assertEquals(((Long) row.getValue(7)) != 0L, a.b);

        row = new ListBasedRow(Collections.<Object>singletonList(1D));
        adapter = new DecisionRowAdaptor(ConvertType.class, new SwiftMetaDataImpl("ConvertType", Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumn("o", Types.BIGINT))));
        ConvertType convertType = (ConvertType) adapter.apply(row);
        assertEquals(row.getValue(0), convertType.o);
    }

    @Test
    public void applyLogToRow() throws Exception {
        A a = new A();
        Function<Object, Row> adapter = new SwiftRowAdaptor(A.class, new SwiftMetaDataImpl("A",
                Arrays.<SwiftMetaDataColumn>asList(
                        new MetaDataColumn("s", Types.BIGINT),
                        new MetaDataColumn("l", Types.BIGINT),
                        new MetaDataColumn("d1", Types.DOUBLE),
                        new MetaDataColumn("d2", Types.DOUBLE),
                        new MetaDataColumn("str", Types.VARCHAR),
                        new MetaDataColumn("utilDate", Types.DATE),
                        new MetaDataColumn("sqlDate", Types.DATE),
                        new MetaDataColumn("b", Types.BIGINT))));
        Row row = adapter.apply(a);
        assertEquals(8, row.getSize());
        assertEquals(((long) a.s), row.getValue(0));
        assertEquals(a.l, row.getValue(1));
        assertEquals(a.d1, row.getValue(2));
        assertEquals(a.d2, row.getValue(3));
        assertEquals(a.str, row.getValue(4));
        assertEquals(a.utilDate.getTime(), row.getValue(5));
        assertEquals(a.sqlDate.getTime(), row.getValue(6));
        assertEquals(a.b ? 1L : 0L, row.getValue(7));

        ConvertType convertType = new ConvertType();
        convertType.o = new Object();
        adapter = new SwiftRowAdaptor(ConvertType.class, new SwiftMetaDataImpl("ConvertType", Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumn("o", Types.BIGINT))));
        row = adapter.apply(convertType);
        assertEquals(1, row.getSize());
        assertEquals(1L, row.getValue(0));
    }
}