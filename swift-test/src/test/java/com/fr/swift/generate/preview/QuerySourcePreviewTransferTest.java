package com.fr.swift.generate.preview;

import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pony on 2017/12/29.
 */
public class QuerySourcePreviewTransferTest extends TestCase {

    public void testCreateResultSet() throws Exception {
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        QueryDBSource source = new QueryDBSource("select 合同ID from DEMO_CAPITAL_RETURN", "demo");
        QuerySourcePreviewTransfer transfer = new QuerySourcePreviewTransfer(connectionInfo, 10, source.getQuery());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()) {
            resultSet.getRowData();
            index++;
        }
        SwiftMetaData metaData = resultSet.getMetaData();
        resultSet.close();
        assertEquals(metaData.getColumnCount(), 1);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnType(1), Types.VARCHAR);
        assertEquals(index, 10);
    }

    public void testCreatePartResultSet() throws Exception {
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        Map<String, ColumnType> fields = new HashMap<String, ColumnType>();
        fields.put("合同ID", ColumnType.NUMBER);
        QuerySourcePreviewTransfer transfer = new QuerySourcePreviewTransfer(connectionInfo, fields,
                1000, "select * from DEMO_CAPITAL_RETURN");
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()) {
            Object ob = resultSet.getRowData().getValue(0);
            assert (ob == null || ob.getClass() == Double.class);
            index++;
        }
        SwiftMetaData metaData = resultSet.getMetaData();
        resultSet.close();
        assertEquals(metaData.getColumnCount(), 1);
        assertEquals(metaData.getColumnName(1), "合同ID");
        assertEquals(metaData.getColumnType(1), Types.DOUBLE);
        assertEquals(index, 682);
    }
}