package com.fr.swift.adaptor.log.query;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferProvider;
import org.junit.Before;

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

    Database db;

    @Before
    public void setUp() throws Exception {
        SwiftContext.get().init();
        new LocalSwiftServerService().start();
        SwiftContext.get().getBean("swiftAnalyseService", AnalyseService.class).start();
        db = com.fr.swift.db.impl.SwiftDatabase.getInstance();
    }

    protected void transportAndIndex(DataSource dataSource, Table table) throws Exception {
        SwiftSourceTransfer transfer = SwiftContext.get().getBean(SwiftSourceTransferProvider.class).createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        ProxySelector.getInstance().getFactory().getProxy(RealtimeService.class).insert(table.getSourceKey(), resultSet);

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
        SegmentKey segmentKey = new SegmentKeyBean(table.getSourceKey().getId(), order, storeType, SwiftDatabase.DECISION_LOG);
        configSegment.add(segmentKey);
        return SegmentUtils.newSegment(location, table.getMeta());
    }

}
