package com.fr.swift.collate;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.Preparer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description 增量块合并成历史块。合并时剔除增量删除掉的数据
 * @since Advanced FineBI 5.0
 */
public class RealtimeCollateTest extends BaseTest {
    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Preparer.prepareCubeBuild(getClass());
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();
    }

    @Test
    public void testAutoRealtimeCollate() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testRealtimeCollate");
        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(dataSource.getSourceKey().getId());
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
        incrementer.insertData(resultSet);
        List<SegmentKey> segKeys = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());

        Where where = new SwiftWhere(HistoryCollateTest.createEqualFilter("合同类型", "购买合同"));
        //合并前7块增量块，且只要allshow是购买合同
        assertEquals(7, segKeys.size());
        for (SegmentKey segKey : segKeys) {
            Segment segment = swiftSegmentManager.getSegment(segKey);
            Decrementer decrementer = new Decrementer(segKey);
            decrementer.delete(where);
            assertSame(segment.getLocation().getStoreType(), Types.StoreType.MEMORY);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            int neqCount = 0;
            for (int i = 0; i < segment.getRowCount(); i++) {
                if (segment.getAllShowIndex().contains(i)) {
                    Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                } else {
                    neqCount++;
                }
            }
            assertTrue(neqCount != 0);
        }
        //合并增量块，直接写history
        SwiftCollateService collaterService = SwiftContext.get().getBean(SwiftCollateService.class);
        collaterService.autoCollateRealtime(dataSource.getSourceKey());
        Thread.sleep(5000L);
        List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        assertEquals(1, segments.size());
        //合并后1块历史块，所有数据都不是购买合同
        for (Segment segment : segments) {
            assertSame(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            for (int i = 0; i < segment.getRowCount(); i++) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
    }

}
