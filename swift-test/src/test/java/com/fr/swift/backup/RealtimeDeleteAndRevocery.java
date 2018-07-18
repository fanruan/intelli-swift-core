package com.fr.swift.backup;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
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
import com.fr.swift.segment.recover.FileSegmentRecovery;
import com.fr.swift.segment.recover.RedisSegmentRecovery;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * This class created on 2018/7/5
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeDeleteAndRevocery extends BaseTest {

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
    public void testFileDeleteAndRecovery() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testFileDeleteAndRecovery");
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource);
        incrementer.increment(resultSet);
        Segment segment = swiftSegmentManager.getSegment(dataSource.getSourceKey()).get(0);

        QueryCondition eqQueryCondition = SwiftQueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
        Where where = new SwiftWhere(eqQueryCondition);

        Decrementer decrementer = new Decrementer(dataSource.getSourceKey(), segment);
        decrementer.delete(where);
        //增量删除后内存数据判断
        Column column = segment.getColumn(new ColumnKey("合同类型"));
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
        //清空内存数据，并恢复数据和allshowindex
        ResourceDiscovery.getInstance().removeCubeResource("cubes/" + dataSource.getSourceKey().getId());
        FileSegmentRecovery recovery = new FileSegmentRecovery();
        List<SegmentKey> segKeys = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());
        recovery.recover(segKeys);

        segment = swiftSegmentManager.getSegment(dataSource.getSourceKey()).get(0);
        column = segment.getColumn(new ColumnKey("合同类型"));
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
    }

    @Test
    public void testRedisDeleteAndRecovery() throws Exception {
        redisClient.flushDB();
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testRedisDeleteAndRecovery");
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource);
        incrementer.increment(resultSet);
        Segment segment = swiftSegmentManager.getSegment(dataSource.getSourceKey()).get(0);

        QueryCondition eqQueryCondition = SwiftQueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
        Where where = new SwiftWhere(eqQueryCondition);

        Decrementer decrementer = new Decrementer(dataSource.getSourceKey(), segment);
        decrementer.delete(where);
        //增量删除后内存数据判断
        Column column = segment.getColumn(new ColumnKey("合同类型"));
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
        //清空内存数据，并恢复数据和allshowindex
        ResourceDiscovery.getInstance().removeCubeResource("cubes/" + dataSource.getSourceKey().getId());
        RedisSegmentRecovery recovery = new RedisSegmentRecovery();
        List<SegmentKey> segKeys = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());
        recovery.recover(segKeys);

        segment = swiftSegmentManager.getSegment(dataSource.getSourceKey()).get(0);
        column = segment.getColumn(new ColumnKey("合同类型"));
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
            }
        }
    }
}
