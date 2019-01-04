package com.fr.swift.source.db;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/12/29.
 */
public class QuerySourceTransferTest extends TestCase {

//    public void testCreateResultSet() throws Exception{
//        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
//        QueryDBSource source = new QueryDBSource("select 合同ID from DEMO_CAPITAL_RETURN", "demo");
//        QuerySourceTransfer transfer = new QuerySourceTransfer(connectionInfo, source.getMetadata(), source.getOuterMetadata(),  source.getQuery());
//        SwiftResultSet resultSet = transfer.createResultSet();
//        int index = 0;
//        while (resultSet.hasNext()) {
//            resultSet.getNextRow();
//            index++;
//        }
//        resultSet.close();
//        assertEquals(index, 682);
//    }
//
//    public void testCreatePartResultSet() throws Exception{
//        ConnectionInfo connectionInfo = TestConnectionProvider.createConnection();
//        LinkedHashMap<String, ColumnType> fields = new LinkedHashMap<>();
//        fields.put("合同ID", ColumnType.NUMBER);
//        QueryDBSource source = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "demo", fields);
//        QuerySourceTransfer transfer = new QuerySourceTransfer(connectionInfo, source.getMetadata(), source.getOuterMetadata(),  source.getQuery());
//        SwiftResultSet resultSet = transfer.createResultSet();
//        int index = 0;
//        while (resultSet.hasNext()) {
//            Object ob = resultSet.getNextRow().getValue(0);
//            assert(ob == null || ob.getClass() == Double.class);
//            index++;
//        }
//        resultSet.close();
//        assertEquals(index, 682);
//    }
}