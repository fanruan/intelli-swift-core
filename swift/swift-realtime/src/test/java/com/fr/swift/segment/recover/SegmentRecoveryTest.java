//package com.fr.swift.segment.recover;
//
//import com.fr.swift.SwiftContext;
//import com.fr.swift.cube.io.ResourceDiscovery;
//import com.fr.swift.cube.io.Types.StoreType;
//import com.fr.swift.cube.io.location.IResourceLocation;
//import com.fr.swift.cube.io.location.ResourceLocation;
//import com.fr.swift.segment.Incrementer;
//import com.fr.swift.segment.RealTimeSegmentImpl;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.column.ColumnKey;
//import com.fr.swift.result.SwiftResultSet;
//import com.fr.swift.source.db.ConnectionInfo;
//import com.fr.swift.source.db.QueryDBSource;
//import com.fr.swift.source.db.QuerySourceTransfer;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TestRule;
//
//import java.lang.reflect.Method;
//
///**
// * @author anchore
// * @date 2018/5/25
// */
//public class SegmentRecoveryTest {
//
//    private QueryDBSource dataSource;
//
//    private ConnectionInfo connectionInfo;
//
//    @Rule
//    public TestRule getExternalResource() throws Exception {
//        Class<?> connClass = Class.forName("com.fr.swift.test.external.ConnectionResource");
//        Method createConnection = connClass.getDeclaredMethod("createConnection");
//        createConnection.setAccessible(true);
//        connectionInfo = (ConnectionInfo) createConnection.invoke(null);
//        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", SegmentRecoveryTest.class.getName());
//    }
//
//    @Test
//    public void recover() throws Exception {
//        QuerySourceTransfer transfer = new QuerySourceTransfer(connectionInfo, dataSource.getMetadata(), dataSource.getMetadata(), dataSource.getQuery());
//        SwiftResultSet swiftResultSet = transfer.createResultSet();
//
//        Incrementer incrementer = new Incrementer(dataSource);
//        incrementer.insertData(swiftResultSet);
//
//        final String tablePath = String.format("%s/%s",
//                dataSource.getMetadata().getSwiftDatabase().getDir(),
//                dataSource.getSourceKey().getId());
//        ResourceDiscovery.getInstance().release(new ResourceLocation(tablePath, StoreType.MEMORY));
//        SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
//        segmentRecovery.recoverAll();
//
//        String cubePath = tablePath + "/seg0";
//        IResourceLocation location = new ResourceLocation(cubePath, StoreType.MEMORY);
//        Segment segment = new RealTimeSegmentImpl(location, dataSource.getMetadata());
//        for (int i = 0; i < segment.getRowCount(); i++) {
//            segment.getColumn(new ColumnKey("付款金额")).getDetailColumn().get(i);
//        }
//    }
//}