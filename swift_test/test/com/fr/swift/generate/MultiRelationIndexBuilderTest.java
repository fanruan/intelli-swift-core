package com.fr.swift.generate;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.history.MultiRelationIndexBuilder;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.utils.BIMultiRelationHelper;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.IRelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.relation.RelationSource;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/2/5
 */
public class MultiRelationIndexBuilderTest extends TestCase {

    CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void setUp() {
        new LocalSwiftServerService().start();

        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        ConnectionManager.getInstance().registerConnectionInfo("allTest", TestConnectionProvider.createConnection());
    }

    /**
     * 全量更新取数索引
     *
     * @throws Exception
     */
    public void testTransport() throws Exception {
//        IndexStuffProvider provider = ProviderManager.getManager().poll();
        DataSource dataSource = new TableDBSource("DEMO_CONTRACT", "allTest");
        DataSource contract = new TableDBSource("DEMO_CAPITAL_RETURN", "allTest");

        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.key();
            if (taskKey.operation() == Operation.NULL) {
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(BaseWorker.nullWorker());
                return wt;
            }

            Object o = pair.value();
            if (o instanceof DataSource) {
                DataSource ds = ((DataSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new TableBuilder(ds));
                return wt;
            } else if (o instanceof IRelationSource) {
                IRelationSource ds = ((IRelationSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new MultiRelationIndexBuilder(BIMultiRelationHelper.convert2CubeRelation(ds), LocalSegmentProvider.getInstance()));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance().initListener();

        List<DataSource> dataSources = new ArrayList<DataSource>();
        dataSources.add(dataSource);
        dataSources.add(contract);
        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey("start all")),
                end = new SchedulerTaskImpl(new CubeTaskKey("end all"));
        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

//        for (DataSource updateDataSource : dataSources) {
        SchedulerTask dataSourceTask = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
        start.addNext(dataSourceTask);
        dataSourceTask.addNext(end);
        l.add(new Pair<>(dataSourceTask.key(), dataSource));


        SchedulerTask contractTask = new SchedulerTaskImpl(new CubeTaskKey(contract.getMetadata().getTableName(), Operation.BUILD_TABLE));
        dataSourceTask.addNext(contractTask);
        contractTask.addNext(end);
        l.add(new Pair<>(contractTask.key(), contract));
//        }

        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        primaryFields.add("总金额");
        foreignFields.add("合同ID");
        foreignFields.add("付款金额");
        IRelationSource relationSource = new RelationSource(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
        CubeTaskKey key = new CubeTaskKey("relation", Operation.INDEX_RELATION);
        SchedulerTask task = new SchedulerTaskImpl(key);
        contractTask.addNext(task);
        task.addNext(end);
        l.add(new Pair<>(key, relationSource));
        SchedulerTaskPool.sendTasks(l);
        start.triggerRun();

        end.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });

        latch.await();
//        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
//        assertEquals(segmentList.size(), 1);
//        Segment segment = segmentList.get(0);
//        assertTrue(segment instanceof HistorySegmentImpl);
//        assertEquals(segment.getRowCount(), 682);
//        assertTrue(segment.getAllShowIndex().contains(0));
//        assertTrue(segment.getAllShowIndex().contains(681));
//        assertFalse(segment.getAllShowIndex().contains(682));
//        try {
//            for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
//                String columnName = dataSource.getMetadata().getColumnName(i);
//                Column column = segment.getColumn(new ColumnKey(columnName));
//                assertNotNull(column.getBitmapIndex().getBitMapIndex(1));
//                assertNotNull(column.getDictionaryEncodedColumn().getIndexByRow(1));
//                assertNotNull(column.getDetailColumn().get(1));
//            }
//        } catch (Exception e) {
//            assertTrue(false);
//        }
    }

    public void testWork() {
        SourceKey primaryTable = new SourceKey("adf482df");
        SourceKey foreignTable = new SourceKey("b727fa70");
        List<ColumnKey> primaryFields = new ArrayList<ColumnKey>();
        List<ColumnKey> foreignFields = new ArrayList<ColumnKey>();
        primaryFields.add(new ColumnKey("brand_id"));
        foreignFields.add(new ColumnKey("brand_id"));
        CubeMultiRelation relation = new CubeMultiRelation(new CubeLogicColumnKey(primaryFields), new CubeLogicColumnKey(foreignFields), primaryTable, foreignTable);
//        new MultiRelationIndexBuilder(relation, LocalSegmentProvider.getInstance()).work();
        List<Segment> primarySegments = LocalSegmentProvider.getInstance().getSegment(primaryTable);
        List<Segment> foreignSegements = LocalSegmentProvider.getInstance().getSegment(foreignTable);
        for (int i = 0; i < primarySegments.size(); i++) {
            Segment segment = primarySegments.get(i);
            for (int k = 0; k < foreignSegements.size(); k++) {
                Segment foreign = foreignSegements.get(k);
                Column column = segment.getColumn(primaryFields.get(0));
                DictionaryEncodedColumn dicColumn = column.getDictionaryEncodedColumn();
                int len = dicColumn.size();
                RelationIndex index = foreign.getRelation(relation);
                for (int j = 0; j < len; j++) {
                    System.out.println(index.getIndex(j));
                }
                int size = foreign.getRowCount();
                for (int j = 0; j < size; j++) {
                    System.out.println(index.getReverseIndex(j));
                }
                System.out.println(index.getNullIndex());
            }
        }
    }
}