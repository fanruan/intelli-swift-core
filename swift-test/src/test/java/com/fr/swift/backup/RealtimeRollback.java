package com.fr.swift.backup;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.LimitedResultSet;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.transatcion.TransactionProxyFactory;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2018/6/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeRollback extends BaseTest {

    private RedisClient redisClient;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SwiftContext.init();
        redisClient = (RedisClient) SwiftContext.get().getBean("redisClient");
    }


    /**
     * 插入的是新块的话，回滚到内存所有都是空的
     */
    @Test
    public void testRollbackWithNewSeg() {
        try {
            redisClient.flushDB();
            DataSource dataSource = new QueryDBSource("select * from DEMO_HR_USER", "RealSwiftInserterTest");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new TestIncrementer(dataSource);
            incrementer.increment(resultSet);

            SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
            Segment segment = localSegmentProvider.getSegment(dataSource.getSourceKey()).get(0);

            assertEquals(redisClient.llen("backup_cubes/7bc94acd/seg0"), 0);
            //rowcount和索引都不会回滚
            assertEquals(segment.getRowCount(), 42);
            Column column = segment.getColumn(new ColumnKey("USER_NAME"));
            int size = column.getDictionaryEncodedColumn().size();
            assertNotSame(size, 0);
            for (int i = 0; i < size; i++) {
                try {
                    ImmutableBitMap indexMap = column.getBitmapIndex().getBitMapIndex(i);
                    assertTrue(true);
                } catch (Exception e) {
                    assertTrue(false);
                }
            }

            //allshow回滚。
            assertTrue(!segment.getAllShowIndex().contains(0));
            assertTrue(!segment.getAllShowIndex().contains(41));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * 插入的是不是新块，则回滚到上次的rowcount/allshow/index等。
     */
    @Test
    public void testRollbackWithOldSeg() {
        try {
            redisClient.flushDB();
            DataSource dataSource = new QueryDBSource("select * from DEMO_HR_USER", "RealSwiftInserterTest");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new Incrementer(dataSource);
            incrementer.increment(resultSet);

            SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
            Segment segment = localSegmentProvider.getSegment(dataSource.getSourceKey()).get(0);

            assertEquals(redisClient.llen("backup_cubes/7bc94acd/seg0"), 42);
            assertEquals(segment.getRowCount(), 42);
            assertTrue(segment.getAllShowIndex().contains(0));
            assertTrue(segment.getAllShowIndex().contains(41));
            Map<String, Map<Integer, ImmutableBitMap>> indexMap = new HashMap<>();
            for (String columnName : segment.getMetaData().getFieldNames()) {
                indexMap.put(columnName, new HashMap<>());
                Column column = segment.getColumn(new ColumnKey(columnName));
                int size = column.getDictionaryEncodedColumn().size();
                for (int i = 0; i < size; i++) {
                    indexMap.get(columnName).put(i, column.getBitmapIndex().getBitMapIndex(i));
                }
            }

            //报错回滚
            transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            resultSet = transfer.createResultSet();
            incrementer = new TestIncrementer(dataSource);
            incrementer.increment(resultSet);

            assertEquals(redisClient.llen("backup_cubes/7bc94acd/seg0"), 42);
            //rowcount不回滚
            assertEquals(segment.getRowCount(), 84);
            //allshow回滚
            assertTrue(segment.getAllShowIndex().contains(0));
            assertTrue(segment.getAllShowIndex().contains(41));
            assertTrue(!segment.getAllShowIndex().contains(42));
            assertTrue(!segment.getAllShowIndex().contains(83));

            Map<String, Map<Integer, ImmutableBitMap>> newIndexMap = new HashMap<>();
            for (String columnName : segment.getMetaData().getFieldNames()) {
                newIndexMap.put(columnName, new HashMap<>());
                Column column = segment.getColumn(new ColumnKey(columnName));
                int size = column.getDictionaryEncodedColumn().size();
                for (int i = 0; i < size; i++) {
                    newIndexMap.get(columnName).put(i, column.getBitmapIndex().getBitMapIndex(i));
                }
            }
            //比较indexMap和newIndexMap，indexMap是回滚前的引用，newIndexMap是回滚后的。回滚前后都一样
            //如 oldmap是{1，2，43，44}，newmap也是
            for (Map.Entry<String, Map<Integer, ImmutableBitMap>> entry : newIndexMap.entrySet()) {
                Map<Integer, ImmutableBitMap> oldMap = indexMap.get(entry.getKey());
                Map<Integer, ImmutableBitMap> newMap = entry.getValue();
                assertEquals(oldMap.size(), newMap.size());
                for (Integer key : oldMap.keySet()) {
                    for (int i = 0; i < 83; i++) {
                        ImmutableBitMap oldBitMap = oldMap.get(key);
                        ImmutableBitMap newBitMap = newMap.get(key);
                        if (newBitMap.contains(i)) {
                            assertTrue(oldBitMap.contains(i));
                        }
                    }

                }
            }
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }


    class TestIncrementer extends Incrementer {
        private final SwiftSegmentManager LOCAL_SEGMENT_PROVIDER = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        private SwiftSourceAlloter alloter;
        private DataSource dataSource;
        private Segment currentSeg;

        public TestIncrementer(DataSource dataSource) {
            super(dataSource);
            this.dataSource = dataSource;
            alloter = new LineSourceAlloter(dataSource.getSourceKey());
        }

        @Override
        public void increment(SwiftResultSet resultSet) throws SQLException {
            if (!SwiftDatabase.getInstance().existsTable(dataSource.getSourceKey())) {
                SwiftDatabase.getInstance().createTable(dataSource.getSourceKey(), dataSource.getMetadata());
            }

            int count = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey()).size();
            boolean newSeg = nextSegment();
            SwiftRealtimeInserter swiftRealtimeInserter = new TestSwiftRealtimeInserter(currentSeg);
            TransactionProxyFactory proxyFactory = new TransactionProxyFactory(swiftRealtimeInserter.getSwiftBackup().getTransactionManager());
            Inserter inserter = (Inserter) proxyFactory.getProxy(swiftRealtimeInserter);
            int step = ((LineAllotRule) alloter.getAllotRule()).getStep();
            int limit = CubeUtil.isReadable(currentSeg) ? step - currentSeg.getRowCount() : step;
            try {
                inserter.insertData(new LimitedResultSet(resultSet, limit));
            } catch (Exception e) {
                e.printStackTrace();
            }

            IResourceLocation location = currentSeg.getLocation();
            SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey().getId(),
                    location.getUri(), count++, location.getStoreType(), currentSeg.getMetaData().getSwiftSchema());
            if (!SwiftSegmentServiceProvider.getProvider().containsSegment(segKey)) {
                SwiftSegmentServiceProvider.getProvider().addSegments(Collections.singletonList(segKey));
            }
        }

        private boolean nextSegment() {
            List<SegmentKey> segmentKeys = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey());
            if (segmentKeys.isEmpty() ||
                    segmentKeys.get(segmentKeys.size() - 1).getStoreType() != Types.StoreType.MEMORY) {
                currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), segmentKeys.size());
                return true;
            }
            currentSeg = LOCAL_SEGMENT_PROVIDER.getSegment(segmentKeys.get(segmentKeys.size() - 1));
            return false;
        }

        private Segment newRealtimeSegment(SegmentInfo segInfo, int segCount) {
            String segPath = String.format("%s/seg%d", CubeUtil.getTablePath(dataSource), segCount + segInfo.getOrder());
            return new RealTimeSegmentImpl(new ResourceLocation(segPath, Types.StoreType.MEMORY), dataSource.getMetadata());
        }
    }

    class TestSwiftRealtimeInserter extends SwiftRealtimeInserter {

        public TestSwiftRealtimeInserter(Segment segment) {
            super(segment);
        }

        public TestSwiftRealtimeInserter(Segment segment, List<String> fields) {
            super(segment, fields);
        }

        @Override
        protected void release() {
            super.release();
            System.out.println(1 / 0);
        }
    }
}
