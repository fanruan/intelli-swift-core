package com.fr.swift.adaptor.preview;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pony on 2017/12/29.
 */
public class TableDBSourcePreviewTransferTest extends TestCase {

    public void testCreateResultSet() throws Exception {
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        TableDBSource source = new TableDBSource("DEMO_CAPITAL_RETURN", "demo");
        TableDBSourcePreviewTransfer transfer = new TableDBSourcePreviewTransfer(connectionInfo, 10, source.getDBTableName());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()) {
            resultSet.getRowData();
            index++;
        }
        SwiftMetaData metaData = resultSet.getMetaData();
        resultSet.close();
        assertEquals(metaData.getColumnCount(), 4);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnName(2), "付款时间");
        assertEquals(metaData.getColumnName(3), "付款金额");
        assertEquals(metaData.getColumnName(4), "记录人");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);
        assertEquals(metaData.getColumnType(2), Types.TIMESTAMP);
        assertEquals(metaData.getColumnType(3), Types.INTEGER);
        assertEquals(metaData.getColumnType(4), Types.VARCHAR);
        assertEquals(index, 10);
    }

    public void testCreatePartResultSet() throws Exception {
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        TableDBSource source = new TableDBSource("DEMO_CAPITAL_RETURN", "demo");
        Map<String, ColumnType> fieldClassTypes = new HashMap<String, ColumnType>();
        fieldClassTypes.put("付款金额", ColumnType.STRING);
        TableDBSourcePreviewTransfer transfer = new TableDBSourcePreviewTransfer(connectionInfo, fieldClassTypes, 10, source.getDBTableName());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()) {
            assertEquals(resultSet.getRowData().getValue(0).getClass(), String.class);
            index++;
        }
        SwiftMetaData metaData = resultSet.getMetaData();
        resultSet.close();
        assertEquals(metaData.getColumnCount(), 1);
        assertEquals(metaData.getColumnName(1), "付款金额");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);
        assertEquals(index, 10);
    }
}