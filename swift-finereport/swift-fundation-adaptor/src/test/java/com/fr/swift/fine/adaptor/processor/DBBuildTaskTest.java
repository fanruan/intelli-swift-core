//package com.fr.swift.fine.adaptor.processor;
//
//import com.finebi.base.constant.FineEngineType;
//import com.finebi.conf.env.LocalEnv;
//import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
//import com.finebi.conf.internalimp.connection.FineConnectionImp;
//import com.finebi.conf.structure.bean.connection.FineConnection;
//import com.finebi.conf.structure.bean.table.FineBusinessTable;
//import com.finebi.conf.structure.result.BIDetailTableResult;
//import com.finebi.conf.utils.FineConnectionUtils;
//import com.fr.base.FRContext;
//import com.finebi.conf.imp.SwiftTableEngineExecutor;
//import com.finebi.conf.imp.SwiftUpdateManager;
//import com.fr.swift.manager.ConnectionProvider;
//import com.fr.swift.manager.ProviderTaskManager;
//import com.fr.swift.resource.ResourceUtils;
//import com.fr.swift.service.LocalSwiftServerService;
//import com.fr.swift.source.db.ConnectionManager;
//import com.fr.swift.source.db.IConnectionProvider;
//import junit.framework.TestCase;
//
///**
// * This class created on 2018-1-16 16:54:06
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class DBBuildTaskTest extends TestCase {
//
//    @Override
//    protected void setUp() throws Exception {
//        new LocalSwiftServerService().start();
//        ProviderTaskManager.start();
//
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
//
//        IConnectionProvider connectionProvider = new ConnectionProvider();
//        ConnectionManager.getInstance().registerProvider(connectionProvider);
//        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
//        FineConnection connection = new FineConnectionImp("jdbc:h2://" + path + "/test", "sa", "", "org.h2.Driver", "DBBuildTaskTest", null, null, null);
//        FineConnectionUtils.removeAllConnections();
//        FineConnectionUtils.addNewConnection(connection);
//    }
//
//    public void testBuildDBTable() throws Exception {
//        FineBusinessTable fineBusinessTable = new FineDBBusinessTable("DEMO_CAPITAL_RETURN1", FineEngineType.Cube, "DBBuildTaskTest", "DEMO_CAPITAL_RETURN");
//        SwiftUpdateManager manager = new SwiftUpdateManager();
//        manager.saveUpdateSetting(null, fineBusinessTable);
//
//        Thread.sleep(10000l);
//        SwiftTableEngineExecutor executor = new SwiftTableEngineExecutor();
//
//        BIDetailTableResult result = executor.getPreviewData(fineBusinessTable, 1000);
//        assertTrue(true);
//        assertEquals(result.columnSize(), 4);
////        assertEquals(result.rowSize(), 682);
//
//        int count = 0;
//        while (result.hasNext()) {
//            result.next();
//            count++;
//        }
//        assertEquals(count, 682);
//    }
//}
