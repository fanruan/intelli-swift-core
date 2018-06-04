package com.fr.swift.fine.adaptor.executor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.impl.SwiftTableEngineExecutor;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.manager.ConnectionProvider;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * This class created on 2018-1-2 15:35:37
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDBEngineExecutorTest extends TestCase {

    private ConnectionInfo connectionInfo;

    private FineDBBusinessTable fineDBBusinessTable = new FineDBBusinessTable("DEMO_CONTRACT", FineEngineType.Cube, "local", "DEMO_CONTRACT", "DEMO_CONTRACT");

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        connectionInfo = TestConnectionProvider.createConnection();
//        ConnectionManager.getInstance().registerConnectionInfo("local", connectionInfo);
    }


    @Test
    public void testSwiftDBDataModelGetFields() throws Exception {
        SwiftTableEngineExecutor dataModel = new SwiftTableEngineExecutor();
        List<FineBusinessField> list = dataModel.getFieldList(fineDBBusinessTable);
        assertEquals(list.size(), 10);
        assertTrue(true);
    }

    @Test
    public void testSwiftDBDataModelPreviewDBTable() throws Exception {
        SwiftTableEngineExecutor dataModel = new SwiftTableEngineExecutor();
        BIDetailTableResult detailTableResult = dataModel.getPreviewData(fineDBBusinessTable, 150);
        assertEquals(detailTableResult.columnSize(), 10);
        int count = 0;
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> cellList = detailTableResult.next();
            assertEquals(cellList.size(), 10);
            count++;
        }
        assertEquals(count, 150);
        assertTrue(true);
    }
}
