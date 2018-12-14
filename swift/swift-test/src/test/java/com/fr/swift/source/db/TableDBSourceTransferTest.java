package com.fr.swift.source.db;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import junit.framework.TestCase;

import java.util.LinkedHashMap;

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
        while (resultSet.hasNext()) {
            resultSet.getNextRow();
            index++;
        }
        resultSet.close();
        assertEquals(index, 838);
    }

    public void testCreatePartResultSet() throws Exception{
        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<>();
        fields.put("JGID", ColumnType.STRING);
        TableDBSource source = new TableDBSource("BANK", "demo", fields);
        TableDBSourceTransfer transfer = new TableDBSourceTransfer(connectionInfo, source.getMetadata(), source.getOuterMetadata(), source.getDBTableName());
        SwiftResultSet resultSet = transfer.createResultSet();
        int index = 0;
        while (resultSet.hasNext()) {
            assertEquals(resultSet.getNextRow().getValue(0).getClass(), String.class);
            index++;
        }
        resultSet.close();
        assertEquals(index, 838);
    }
}