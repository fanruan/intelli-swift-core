package com.fr.swift.source.db;

import com.fr.swift.source.SwiftMetaData;
import junit.framework.TestCase;

import java.sql.Types;

/**
 * Created by pony on 2017/12/29.
 */
public class TableDBSourceTest extends TestCase {
    private TableDBSource source;
    private TableDBSource partSource;
//    public void setUp() throws Exception{
//        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
//        source = new TableDBSource("DEMO_CAPITAL_RETURN", "demo");
//        Map<String, ColumnType> fields = new HashMap<String, ColumnType>();
//        fields.put("合同ID", ColumnType.NUMBER);
//        partSource = new TableDBSource("DEMO_CAPITAL_RETURN", "demo", fields);
//    }

    public void testGetSourceKey() {
        assertEquals("824f42d6", source.getSourceKey().getId());
    }

    public void testGetMetadata() throws Exception{
        SwiftMetaData metaData = source.getMetadata();
        assertEquals(metaData.getColumnCount(), 4);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnName(2), "付款时间");
        assertEquals(metaData.getColumnName(3), "付款金额");
        assertEquals(metaData.getColumnName(4), "记录人");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);
        assertEquals(metaData.getColumnType(2), Types.TIMESTAMP);
        assertEquals(metaData.getColumnType(3), Types.INTEGER);
        assertEquals(metaData.getColumnType(4), Types.VARCHAR);
    }

    public void testGetPartMetadata() throws Exception{
        SwiftMetaData metaData = partSource.getMetadata();
        assertEquals(metaData.getColumnCount(), 1);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnType(1), Types.DOUBLE);
    }
}