//package com.fr.swift.fine.adaptor.update;
//
//import com.finebi.base.constant.FineEngineType;
//import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
//import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
//import com.finebi.conf.internalimp.connection.FineConnectionImp;
//import com.finebi.conf.internalimp.update.TableUpdateInfo;
//import com.finebi.conf.structure.bean.connection.FineConnection;
//import com.finebi.conf.structure.bean.table.FineBusinessTable;
//import com.finebi.conf.utils.FineConnectionUtils;
//import com.finebi.conf.imp.SwiftUpdateManager;
//import com.fr.swift.cube.queue.StuffProviderQueue;
//import com.fr.swift.manager.ConnectionProvider;
//import com.fr.swift.resource.ResourceUtils;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.db.ConnectionManager;
//import com.fr.swift.source.db.IConnectionProvider;
//import com.fr.swift.source.manager.IndexStuffProvider;
//import junit.framework.TestCase;
//
///**
// * This class created on 2018-1-12 16:24:46
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class UpdateTableTest extends TestCase {
//
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        IConnectionProvider connectionProvider = new ConnectionProvider();
//        ConnectionManager.getInstance().registerProvider(connectionProvider);
//        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
//        FineConnection connection = new FineConnectionImp("jdbc:h2://" + path + "/test", "sa", "", "org.h2.Driver", "local", null, null, null);
//        FineConnectionUtils.removeAllConnections();
//        FineConnectionUtils.addNewConnection(connection);
//    }
//
//    public void testDBTableUpdate() throws Exception {
//        FineBusinessTable fineBusinessTable = new FineDBBusinessTable("DEMO_CAPITAL_RETURN1", FineEngineType.Cube, "local", "DEMO_CAPITAL_RETURN");
//        SwiftUpdateManager manager = new SwiftUpdateManager();
//        manager.saveUpdateSetting(null, fineBusinessTable);
//
//        IndexStuffProvider provider = StuffProviderQueue.getQueue().poll();
//        assertTrue(true);
//        assertTrue(provider.getAllTables().size() == 1);
//        try {
//            for (DataSource dataSource : provider.getAllTables()) {
//                assertNotSame(dataSource.getMetadata().getColumnCount(), 0);
//            }
//        } catch (Exception e) {
//            assertTrue(false);
//        }
//
//    }
//
//    public void testSqlTableUpdate() throws Exception {
//        FineBusinessTable fineBusinessTable = new FineSQLBusinessTable("1", "local", FineEngineType.Cube, "select * from DEMO_CAPITAL_RETURN");
//        SwiftUpdateManager manager = new SwiftUpdateManager();
//        manager.saveUpdateSetting(null, fineBusinessTable);
//
//        IndexStuffProvider provider = StuffProviderQueue.getQueue().poll();
//        assertTrue(true);
//        assertTrue(provider.getAllTables().size() == 1);
//        try {
//            for (DataSource dataSource : provider.getAllTables()) {
//                assertNotSame(dataSource.getMetadata().getColumnCount(), 0);
//            }
//        } catch (Exception e) {
//            assertTrue(false);
//        }
//    }
//
//    public void testIncrementUpdate() throws Exception {
//        FineBusinessTable fineBusinessTable = new FineSQLBusinessTable("2", "local", FineEngineType.Cube, "select * from DEMO_CAPITAL_RETURN");
//        SwiftUpdateManager manager = new SwiftUpdateManager();
//        TableUpdateInfo tableUpdateInfo = new TableUpdateInfo();
//        tableUpdateInfo.setUpdateType(1);
//        tableUpdateInfo.setAddSql("select * from a");
//        manager.saveUpdateSetting(tableUpdateInfo, fineBusinessTable);
//
//        IndexStuffProvider provider = StuffProviderQueue.getQueue().poll();
//        assertTrue(true);
//        assertTrue(provider.getAllTables().size() == 1);
//        try {
//            for (DataSource dataSource : provider.getAllTables()) {
//                assertNotSame(dataSource.getMetadata().getColumnCount(), 0);
//            }
//        } catch (Exception e) {
//            assertTrue(false);
//        }
//        assertNotNull(provider.getIncrementBySourceId(provider.getAllTables().get(0).getSourceKey().getId()));
//    }
//}
