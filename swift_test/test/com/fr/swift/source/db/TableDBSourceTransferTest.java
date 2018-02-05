package com.fr.swift.source.db;

import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.SwiftResultSet;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pony on 2017/12/29.
 */
public class TableDBSourceTransferTest extends TestCase {


    public void testCreateResultSet() throws Exception{
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        TableDBSource source = new TableDBSource("BANK", "demo");
        TableDBSourceTransfer transfer = new TableDBSourceTransfer(connectionInfo, source.getMetadata(), source.getOuterMetadata(), source.getDBTableName());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()){
            resultSet.getRowData();
            index++;
        }
        resultSet.close();
        assertEquals(index, 838);
    }

    public void testCreatePartResultSet() throws Exception{
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        Map<String, Integer> fields = new HashMap<>();
        fields.put("JGID", ColumnTypeConstants.COLUMN.STRING);
        TableDBSource source = new TableDBSource("BANK", "demo", fields);
        TableDBSourceTransfer transfer = new TableDBSourceTransfer(connectionInfo, source.getMetadata(), source.getOuterMetadata(), source.getDBTableName());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.next()){
            assertEquals(resultSet.getRowData().getValue(0).getClass(), String.class);
            index++;
        }
        resultSet.close();
        assertEquals(index, 838);
    }
}