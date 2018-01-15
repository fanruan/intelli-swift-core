package com.fr.swift.fine.adaptor.executor;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.base.FRContext;
import com.fr.data.core.db.TableProcedure;
import com.fr.dav.LocalEnv;
import com.fr.swift.adaptor.model.SwiftDBEngineExecutor;
import com.fr.swift.provider.ConnectionProvider;
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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        connectionInfo = TestConnectionProvider.createConnection();
        ConnectionManager.getInstance().registerConnectionInfo("local", connectionInfo);
    }

    @Test
    public void testSwiftGetAllTables() {
        SwiftDBEngineExecutor dataModel = new SwiftDBEngineExecutor();
        List<TableProcedure> list = dataModel.getAllTables(connectionInfo.getFrConnection(), "local", connectionInfo.getSchema());
        assertTrue(list.size() == 7);
        assertTrue(true);
    }

    @Test
    public void testSwiftDBDataModelGetFields() throws Exception {
        SwiftDBEngineExecutor dataModel = new SwiftDBEngineExecutor();
        List<FineBusinessField> list = dataModel.getFieldList("local", "DEMO_CONTRACT", connectionInfo.getSchema(), connectionInfo.getFrConnection());
        assertEquals(list.size(), 10);
        assertTrue(true);
    }

    @Test
    public void testSwiftDBDataModelPreviewDBTable() throws Exception {
        SwiftDBEngineExecutor dataModel = new SwiftDBEngineExecutor();
        BIDetailTableResult detailTableResult = dataModel.getPreviewData("local", "DEMO_CONTRACT", 150, connectionInfo.getSchema(), connectionInfo.getFrConnection());
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
