package com.fr.swift.segment.collate;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.generate.ColumnIndexer;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.insert.HistoryBlockInserter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description 历史块合并成历史块。合并时剔除增量删除掉的数据
 * @since Advanced FineBI 5.0
 */
public class HistoryCollateTest extends TestCase {

    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        Preparer.prepareCubeBuild(getClass());
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();
    }

    @Test
    public void testAutoHistoryCollate() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testHistoryCollate");
        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(dataSource.getSourceKey().getId());
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Inserter inserter = new HistoryBlockInserter(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
        inserter.insertData(resultSet);

        List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        for (String fieldName : dataSource.getMetadata().getFieldNames()) {
            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(fieldName), segments);
            columnIndexer.work();
        }

        //合并前1块历史块，且只要allshow是购买合同
        assertEquals(7, segments.size());
        for (Segment segment : segments) {
            assertSame(segment.getLocation().getStoreType(), StoreType.FINE_IO);
            assertTrue(segment.getRowCount() <= 100);
        }
        //合并历史块，直接写history
        SwiftCollateService collaterService = SwiftContext.get().getBean(SwiftCollateService.class);
        collaterService.autoCollateHistory(dataSource.getSourceKey());
        Thread.sleep(5000L);
        segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        assertEquals(1, segments.size());
        //合并后1块历史块，所有数据都是购买合同
        for (Segment segment : segments) {
            assertSame(segment.getLocation().getStoreType(), StoreType.FINE_IO);
            assertTrue(segment.getRowCount() == 668);
        }
    }

}
