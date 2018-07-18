package com.fr.swift.adaptor.log.query;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.test.Preparer;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.List;

/**
 * This class created on 2018/4/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LogBaseTest {

    static final SwiftLogger LOGGER = SwiftLoggers.getLogger(LogBaseTest.class);

    @BeforeClass
    public static void beforeClass() throws Exception {
        Preparer.prepareCubeBuild();
    }

    @Before
    public void setUp() throws Exception {
        new LocalSwiftServerService().start();
        SwiftContext.get().getBean(AnalyseService.class).start();
        TestConnectionProvider.createConnection();
        TestConfDb.setConfDb();
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
    }

    protected void transportAndIndex(DataSource dataSource, Table table) throws Exception {
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        table.insert(resultSet);

//        IConfigSegment configSegment = new SegmentUnique();
//        configSegment.setSourceKey(table.getSourceKey().getId());
//
//        Segment segment = createSegment(0, Types.StoreType.MEMORY, table, configSegment);
//        SwiftConfigServiceProvider.getInstance().addSegments(configSegment);
//
//        RealtimeSwiftInserter swiftInserter = new RealtimeSwiftInserter(segment);
//        swiftInserter.insertData(resultSet);
//
//        for (int i = 1; i <= table.getMeta().getColumnCount(); i++) {
//            List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(table.getSourceKey());
//
//            ColumnIndexer columnIndexer = new ColumnIndexer(table, new ColumnKey(table.getMeta().getColumnName(i)), segmentList);
//            columnIndexer.work();
//            ColumnDictMerger columnDictMerger = new ColumnDictMerger(table, new ColumnKey(table.getMeta().getColumnName(i)), segmentList);
//            columnDictMerger.work();
//        }
    }

    protected Segment createSegment(int order, Types.StoreType storeType, Table table, List<SegmentKey> configSegment) throws Exception {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + table.getSourceKey().getId() + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath, storeType);
        SegmentKey segmentKey = new SegmentKeyBean(table.getSourceKey().getId(), location.getUri(), order, storeType, SwiftDatabase.Schema.CUBE);
        configSegment.add(segmentKey);
        return new RealTimeSegmentImpl(location, table.getMeta());
    }

}
