package com.fr.swift.generate;

import com.fr.general.ComparatorUtils;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.LongArray;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/2/5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiRelationIndexerTest  {

    CountDownLatch latch = new CountDownLatch(1);
    DataSource dataSource = null;
    DataSource contract = null;

    @Before
    public void setUp() throws Exception {
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.getKey();
            if (taskKey.operation() == Operation.NULL) {
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(BaseWorker.nullWorker());
                return wt;
            }

            Object o = pair.getValue();
            if (o instanceof DataSource) {
                DataSource ds = ((DataSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new TableBuilder(ds));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance().initListener();
        dataSource = new TableDBSource("DEMO_CONTRACT", "allTest");
        contract = new TableDBSource("DEMO_CAPITAL_RETURN", "allTest");
        initConfigDB();
    }

    private void initConfigDB() throws Exception {
        TestConfDb.setConfDb();
    }

    public void buildMultiRelationIndex() throws Exception {
        List<Pair<TaskKey, Object>> l = new ArrayList<>();
        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();
        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));
        List<DataSource> dataSources = new ArrayList<DataSource>();
        dataSources.add(dataSource);
        dataSources.add(contract);
        for (DataSource updateDataSource : dataSources) {
            SchedulerTask task = CubeTasks.newTableTask(updateDataSource);
            start.addNext(task);
            task.addNext(end);
            l.add(new Pair<>(task.key(), updateDataSource));
        }
        end.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        CubeTasks.sendTasks(l);
        start.triggerRun();
        latch.await();
        if (end.result() != Result.SUCCEEDED) {
            TestCase.fail();
        }
        MultiRelationIndexer indexer = new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(createRelation()), LocalSegmentProvider.getInstance());
        SchedulerTask relationTask = CubeTasks.newRelationTask(createRelation());
        WorkerTask task = new WorkerTaskImpl(relationTask.key());
        task.setWorker(indexer);
        task.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        task.run();
        latch.await();
    }

    private RelationSource createRelation() {
        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        primaryFields.add("总金额");
        foreignFields.add("合同ID");
        foreignFields.add("付款金额");
        return new RelationSourceImpl(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
    }

    /**
     * 全量更新取数索引
     *
     * @throws Exception
     */
    @Test
    public void testBuildRelation() throws Exception {
        buildMultiRelationIndex();
        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        List<Segment> foreignList = LocalSegmentProvider.getInstance().getSegment(contract.getSourceKey());
        for (int fi = 0; fi < foreignList.size(); fi++) {
            Segment foreign = foreignList.get(fi);
            CubeMultiRelation relation = RelationPathHelper.convert2CubeRelation(createRelation());
            RelationIndex index = foreign.getRelation(relation);
            List<ColumnKey> primaryKeys = relation.getPrimaryField().getKeyFields();
            List<ColumnKey> foreignKeys = relation.getForeignField().getKeyFields();
            DetailColumn foreign1 = foreign.getColumn(foreignKeys.get(0)).getDetailColumn();
            DetailColumn foreign2 = foreign.getColumn(foreignKeys.get(1)).getDetailColumn();
            int pos = 1;
            for (int pi = 0; pi < segmentList.size(); pi++) {
                Segment segment = segmentList.get(pi);
                TestCase.assertTrue(segment instanceof HistorySegmentImpl);
                TestCase.assertTrue(foreign instanceof HistorySegmentImpl);
                int primaryRow = segment.getRowCount();

                DetailColumn primary1 = segment.getColumn(primaryKeys.get(0)).getDetailColumn();
                DetailColumn primary2 = segment.getColumn(primaryKeys.get(1)).getDetailColumn();

                for (int i = 0; i < primaryRow; i++) {
                    Object p1 = primary1.get(i);
                    Object p2 = primary2.get(i);
                    LongArray targetIndex = index.getIndex(pos++);
                    for (int j = 0; j < targetIndex.size(); j++) {
                        long longValue = targetIndex.get(j);
                        if (!ComparatorUtils.equals(longValue, NIOConstant.LONG.NULL_VALUE)) {
                            int[] target = long2IntArray(longValue);
                            Object f1 = foreign1.get(target[1]);
                            Object f2 = foreign2.get(target[1]);
                            TestCase.assertEquals(p1, f1);
                            TestCase.assertEquals(p2, f2);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testReverse() {
        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        List<Segment> foreignList = LocalSegmentProvider.getInstance().getSegment(contract.getSourceKey());
        CubeMultiRelation relation = RelationPathHelper.convert2CubeRelation(createRelation());
        List<ColumnKey> primaryKeys = relation.getPrimaryField().getKeyFields();
        List<ColumnKey> foreignKeys = relation.getForeignField().getKeyFields();
        for (int fi = 0; fi < foreignList.size(); fi++) {
            Segment foreign = foreignList.get(fi);
            RelationIndex index = foreign.getRelation(relation);
            int rowCount = foreign.getRowCount();
            int count = index.getReverseCount();
            for (int i = 0; i < count; i++) {
                long reverse = index.getReverseIndex(i);
                if (!ComparatorUtils.equals(reverse, NIOConstant.LONG.NULL_VALUE)) {
                    int[] target = long2IntArray(reverse);
                    Segment primarySegment = segmentList.get(target[0]);
                    Object p1 = primarySegment.getColumn(primaryKeys.get(0)).getDetailColumn().get(target[1]);
                    Object p2 = primarySegment.getColumn(primaryKeys.get(1)).getDetailColumn().get(target[1]);
                    Object f1 = foreign.getColumn(foreignKeys.get(0)).getDetailColumn().get(i % rowCount);
                    Object f2 = foreign.getColumn(foreignKeys.get(1)).getDetailColumn().get(i % rowCount);
                    TestCase.assertEquals(p1, f1);
                    TestCase.assertEquals(p2, f2);
                }
            }
        }
    }

    private int[] long2IntArray(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }
}