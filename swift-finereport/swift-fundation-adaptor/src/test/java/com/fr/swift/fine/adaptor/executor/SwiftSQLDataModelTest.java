package com.fr.swift.fine.adaptor.executor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.impl.SwiftTableEngineExecutor;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.manager.ConnectionProvider;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

import java.util.List;

/**
 * This class created on 2018-1-3 10:00:30
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftSQLDataModelTest extends TestCase {

    private String sql;
    private ConnectionInfo connectionInfo;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        connectionInfo = TestConnectionProvider.createConnection();
        sql = "select \n" +
//                "合同ID,\n" +
                "客户ID,\n" +
                "合同类型,\n" +
                "总金额,\n" +
                "合同付款类型,\n" +
                "注册时间,\n" +
                "购买数量,\n" +
                "合同签约时间,\n" +
                "购买的产品,\n" +
                "是否已经交货 \n" +
                "from DEMO_CONTRACT";
    }

    public void testSwiftSQLDataModelGetFields() throws Exception {
        SwiftTableEngineExecutor dataModel = new SwiftTableEngineExecutor();
        FineBusinessTable fineBusinessTable = new FineSQLBusinessTable("A", "local", FineEngineType.Cube, sql, "A");

        List<FineBusinessField> list = dataModel.getFieldList(fineBusinessTable);
        assertEquals(list.size(), 9);
        assertTrue(true);
    }

    public void testSwiftSQLDataModelPreviewDBTable() throws Exception {
        SwiftTableEngineExecutor dataModel = new SwiftTableEngineExecutor();

        FineBusinessTable fineBusinessTable = new FineSQLBusinessTable("A", "local", FineEngineType.Cube, sql, "A");


        BIDetailTableResult detailTableResult = dataModel.getPreviewData(fineBusinessTable, 250);
        assertEquals(detailTableResult.columnSize(), 9);
        int count = 0;
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> cellList = detailTableResult.next();
            assertEquals(cellList.size(), 9);
            count++;
        }
        assertEquals(count, 250);
        assertTrue(true);
    }
}
