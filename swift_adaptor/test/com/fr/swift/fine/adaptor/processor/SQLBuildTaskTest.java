package com.fr.swift.fine.adaptor.processor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.connection.FineConnectionImp;
import com.finebi.conf.internalimp.table.FineSQLBusinessTable;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.adaptor.model.SwiftSQLEngineExecutor;
import com.fr.swift.adaptor.update.SwiftUpdateManager;
import com.fr.swift.manager.ProviderTaskManager;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import junit.framework.TestCase;

/**
 * This class created on 2018-1-17 09:51:25
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SQLBuildTaskTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        new LocalSwiftServerService().start();
        ProviderTaskManager.start();

        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));

        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
        FineConnection connection = new FineConnectionImp("jdbc:h2://" + path + "/test", "sa", "", "org.h2.Driver", "SQLBuildTaskTest", null, null, null);
        FineConnectionUtils.removeAllConnections();
        FineConnectionUtils.addNewConnection(connection);
    }

    public void testBuildSqlTable() throws Exception {
        FineBusinessTable fineBusinessTable = new FineSQLBusinessTable("DEMO_CAPITAL_RETURN1", "SQLBuildTaskTest", FineEngineType.Cube, "select * from DEMO_CAPITAL_RETURN");
        SwiftUpdateManager manager = new SwiftUpdateManager();
        manager.saveUpdateSetting(null, fineBusinessTable);

        Thread.sleep(10000l);
        SwiftSQLEngineExecutor executor = new SwiftSQLEngineExecutor();
        BIDetailTableResult result = executor.getPreviewRealData("SQLBuildTaskTest", "select * from DEMO_CAPITAL_RETURN");
        assertTrue(true);
        assertEquals(result.columnSize(), 4);
        assertEquals(result.rowSize(), 682);

        int count = 0;
        while (result.hasNext()) {
            result.next();
            count++;
        }
        assertEquals(count, 682);
    }
}
