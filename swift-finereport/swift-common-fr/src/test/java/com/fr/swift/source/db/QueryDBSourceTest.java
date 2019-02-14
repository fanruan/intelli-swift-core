package com.fr.swift.source.db;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.LinkedHashMap;

/**
 * Created by pony on 2017/12/29.
 */
public class QueryDBSourceTest extends TestCase {
    private QueryDBSource source;
    private QueryDBSource partSource;
    public void setUp() throws Exception{
        source = new QueryDBSource("select 合同ID from DEMO_CAPITAL_RETURN", "demo");
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<String, ColumnType>();
        fields.put("合同ID", ColumnType.NUMBER);
        partSource = new QueryDBSource("select 合同ID from DEMO_CAPITAL_RETURN", "demo", fields);
    }

    public void testGetSourceKey() {
        assertEquals("6f9909ab", source.getSourceKey().getId());
    }

    public void testGetMetadata() throws Exception{
        SwiftMetaData metaData = source.getMetadata();
        assertEquals(metaData.getColumnCount(), 1);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);

        SwiftMetaData partMeta = partSource.getMetadata();
        assertEquals(partMeta.getColumnCount(), 1);
        assertEquals(partMeta.getColumnName(1), "合同ID");
        assertEquals(partMeta.getColumnType(1), Types.DOUBLE);
    }
}