package com.fr.swift.collate;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.CollateService;
import com.fr.swift.SwiftCollateService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.query.condition.SwiftQueryFactory;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.line.LineAllotRule;
import com.fr.swift.source.alloter.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        redisClient = (RedisClient) SwiftContext.getInstance().getBean("redisClient");
        swiftSegmentManager = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);
    }

    @Test
    public void testAutoRealtimeCollate() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testRealtimeCollate");
            SwiftSegmentServiceProvider.getProvider().removeSegments(dataSource.getSourceKey().getId());
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
            incrementer.increment(resultSet);

            List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
            QueryCondition eqQueryCondition = SwiftQueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
            Where where = new SwiftWhere(eqQueryCondition);
            //合并前7块增量块，且只要allshow是购买合同
            assertTrue(segments.size() == 7);
            for (Segment segment : segments) {
                Decrementer decrementer = new Decrementer(segment);
                decrementer.delete(dataSource.getSourceKey(), where);
                assertTrue(segment.getLocation().getStoreType() == Types.StoreType.MEMORY);
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                int neqCount = 0;
                for (int i = 0; i < segment.getRowCount(); i++) {
                    if (segment.getAllShowIndex().contains(i)) {
                        TestCase.assertEquals(column.getDetailColumn().get(i), "购买合同");
                    } else {
                        neqCount++;
                    }
                }
                assertTrue(neqCount != 0);
            }
            //合并增量块，直接写history
            CollateService collaterService = new SwiftCollateService();
            collaterService.autoCollateRealtime(dataSource.getSourceKey());

            segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
            assertTrue(segments.size() == 1);
            //合并后1块历史块，所有数据都是购买合同
            for (Segment segment : segments) {
                assertTrue(segment.getLocation().getStoreType() == Types.StoreType.FINE_IO);
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                for (int i = 0; i < segment.getRowCount(); i++) {
                    TestCase.assertEquals(column.getDetailColumn().get(i), "购买合同");
                }
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }


    @Test
    public void testAppointRealtimeCollate() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testAppointRealtimeCollate");
            SwiftSegmentServiceProvider.getProvider().removeSegments(dataSource.getSourceKey().getId());
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
            incrementer.increment(resultSet);

            List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
            QueryCondition eqQueryCondition = SwiftQueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
            Where where = new SwiftWhere(eqQueryCondition);
            //合并前7块增量块，且只要allshow是购买合同
            assertTrue(segments.size() == 7);
            for (Segment segment : segments) {
                Decrementer decrementer = new Decrementer(segment);
                decrementer.delete(dataSource.getSourceKey(), where);
                assertTrue(segment.getLocation().getStoreType() == Types.StoreType.MEMORY);
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                int neqCount = 0;
                for (int i = 0; i < segment.getRowCount(); i++) {
                    if (segment.getAllShowIndex().contains(i)) {
                        TestCase.assertEquals(column.getDetailColumn().get(i), "购买合同");
                    } else {
                        neqCount++;
                    }
                }
                assertTrue(neqCount != 0);
            }

            List<SegmentKey> segmentKeyList = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());
            assertTrue(segmentKeyList.size() == 7);
            //取4个进行合并
            List<SegmentKey> collateSegmentKeys = new ArrayList<SegmentKey>();
            collateSegmentKeys.add(segmentKeyList.get(0));
            collateSegmentKeys.add(segmentKeyList.get(1));
            collateSegmentKeys.add(segmentKeyList.get(2));
            collateSegmentKeys.add(segmentKeyList.get(3));


            //合并增量块，直接写history
            CollateService collaterService = new SwiftCollateService();
            collaterService.appointCollateRealtime(collateSegmentKeys);

            segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
            assertTrue(segments.size() == 4);
            //合并后1块历史块,3块增量块，历史块所有数据都是购买合同，增量快allshow是购买合同
            int hisCount = 0;
            int realCount = 0;
            for (Segment segment : segments) {
                if (segment.getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                    Column column = segment.getColumn(new ColumnKey("合同类型"));
                    for (int i = 0; i < segment.getRowCount(); i++) {
                        TestCase.assertEquals(column.getDetailColumn().get(i), "购买合同");
                    }
                    hisCount++;
                } else {
                    Column column = segment.getColumn(new ColumnKey("合同类型"));
                    int neqCount = 0;
                    for (int i = 0; i < segment.getRowCount(); i++) {
                        if (segment.getAllShowIndex().contains(i)) {
                            TestCase.assertEquals(column.getDetailColumn().get(i), "购买合同");
                        } else {
                            neqCount++;
                        }
                    }
                    assertTrue(neqCount != 0);
                    realCount++;
                }
            }
            assertTrue(hisCount == 1);
            assertTrue(realCount == 3);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
