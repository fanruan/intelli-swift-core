package com.fr.swift.collate;

import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.task.service.SwiftServiceTaskExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
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

    private RedisClient redisClient;

    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SwiftContext.init();
        redisClient = (RedisClient) SwiftContext.get().getBean("redisClient");
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
    }

    @Test
    public void testAutoRealtimeCollate() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testRealtimeCollate");
        SwiftSegmentServiceProvider.getProvider().removeSegments(dataSource.getSourceKey().getId());
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
        incrementer.increment(resultSet);

        List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());

        Where where = new SwiftWhere(HistoryCollateTest.createEqualFilter("合同类型", "购买合同"));
        //合并前7块增量块，且只要allshow是购买合同
        assertEquals(7, segments.size());
        for (Segment segment : segments) {
            Decrementer decrementer = new Decrementer(dataSource.getSourceKey(), segment);
            decrementer.delete(where);
            assertSame(segment.getLocation().getStoreType(), StoreType.MEMORY);
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
        SwiftCollateService collaterService = new SwiftCollateService();
        collaterService.setTaskExecutor(new SwiftServiceTaskExecutor("testAutoRealtimeCollate", 1));
        collaterService.autoCollateRealtime(dataSource.getSourceKey());
        Thread.sleep(5000L);
        segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        assertEquals(1, segments.size());
        //合并后1块历史块，所有数据都不是购买合同
        for (Segment segment : segments) {
            assertSame(segment.getLocation().getStoreType(), StoreType.FINE_IO);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            for (int i = 0; i < segment.getRowCount(); i++) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
    }


    @Test
    public void testAppointRealtimeCollate() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testAppointRealtimeCollate");
        SwiftSegmentServiceProvider.getProvider().removeSegments(dataSource.getSourceKey().getId());
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
        incrementer.increment(resultSet);

        List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());

        Where where = new SwiftWhere(HistoryCollateTest.createEqualFilter("合同类型", "购买合同"));
        //合并前7块增量块，且只要allshow是购买合同
        assertEquals(7, segments.size());
        for (Segment segment : segments) {
            Decrementer decrementer = new Decrementer(dataSource.getSourceKey(), segment);
            decrementer.delete(where);
            assertSame(segment.getLocation().getStoreType(), StoreType.MEMORY);
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

        List<SegmentKey> segmentKeyList = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());
        assertEquals(7, segmentKeyList.size());
        //取4个进行合并
        List<SegmentKey> collateSegmentKeys = new ArrayList<SegmentKey>();
        collateSegmentKeys.add(segmentKeyList.get(0));
        collateSegmentKeys.add(segmentKeyList.get(1));
        collateSegmentKeys.add(segmentKeyList.get(2));
        collateSegmentKeys.add(segmentKeyList.get(3));


        //合并增量块，直接写history
        SwiftCollateService collaterService = new SwiftCollateService();
        collaterService.setTaskExecutor(new SwiftServiceTaskExecutor("testAppointRealtimeCollate", 1));
        collaterService.appointCollateRealtime(collateSegmentKeys);

        Thread.sleep(5000L);
        segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        assertEquals(4, segments.size());
        //合并后1块历史块,3块增量块，历史块所有数据都不是购买合同，增量快allshow不是购买合同
        int hisCount = 0;
        int realCount = 0;
        for (Segment segment : segments) {
            if (segment.getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                for (int i = 0; i < segment.getRowCount(); i++) {
                    Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                }
                hisCount++;
            } else {
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
                realCount++;
            }
        }
        assertEquals(1, hisCount);
        assertEquals(3, realCount);
    }
}
