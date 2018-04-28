package com.fr.swift.generate.history;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.BaseConfigTest;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.generate.history.index.TablePathIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.column.RelationColumn;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.LongArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/2/8
 */
public class TablePathIndexerTest extends BaseConfigTest {

    CountDownLatch latch = new CountDownLatch(1);
    DataSource dataSource = null;
    DataSource contract = null;
    DataSource customer = null;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        customer = new TableDBSource("DEMO_CUSTOMER", "allTest");
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
        dataSources.add(customer);


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

        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        RelationSource relationSource = new RelationSourceImpl(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
        MultiRelationIndexer indexer = new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(relationSource), LocalSegmentProvider.getInstance());
        SchedulerTask relationTask = CubeTasks.newRelationTask(relationSource);
        WorkerTask task = new WorkerTaskImpl(relationTask.key());
        task.setWorker(indexer);
        task.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        task.run();
        latch.await();

        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        RelationSource custSource = new RelationSourceImpl(customer.getSourceKey(), dataSource.getSourceKey(), primaryFields1, foreignFields1);
        MultiRelationIndexer custRelationIndexer = new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(custSource), LocalSegmentProvider.getInstance());
        SchedulerTask custRelationTask = CubeTasks.newRelationTask(custSource);
        WorkerTask custTask = new WorkerTaskImpl(custRelationTask.key());
        custTask.setWorker(custRelationIndexer);
        custTask.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        custTask.run();
        latch.await();
    }

    public void test() throws Exception {
        buildMultiRelationIndex();
        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        RelationSource relationSource = new RelationSourceImpl(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);

        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        RelationSource custSource = new RelationSourceImpl(customer.getSourceKey(), dataSource.getSourceKey(), primaryFields1, foreignFields1);
        CubeMultiRelationPath path = new CubeMultiRelationPath();
        path.add(RelationPathHelper.convert2CubeRelation(custSource));
        path.add(RelationPathHelper.convert2CubeRelation(relationSource));
        TablePathIndexer builder = new TablePathIndexer(path, LocalSegmentProvider.getInstance());
        WorkerTask task = new WorkerTaskImpl(CubeTasks.newTaskKey(relationSource));
        task.setWorker(builder);
        task.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });
        task.run();
        latch.await();

        assertResult(path);
    }

    private void assertResult(CubeMultiRelationPath path) {
        List<Segment> firstSegments = LocalSegmentProvider.getInstance().getSegment(customer.getSourceKey());
        List<Segment> secondSegments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        List<Segment> thirdSegments = LocalSegmentProvider.getInstance().getSegment(contract.getSourceKey());
        assertEquals(1, firstSegments.size());
        assertEquals(1, secondSegments.size());
        assertEquals(1, thirdSegments.size());

        Segment first = firstSegments.get(0);
        Segment second = secondSegments.get(0);
        Segment third = thirdSegments.get(0);

        RelationIndex index = third.getRelation(path);
        int firstRow = first.getRowCount();
        List<ColumnKey> firstRelationPrimaryKeys = path.getFirstRelation().getPrimaryField().getKeyFields();
        DetailColumn primary1 = first.getColumn(firstRelationPrimaryKeys.get(0)).getDetailColumn();
        CubeMultiRelation firstRelation = path.getFirstRelation();
        RelationIndex firstIndex = second.getRelation(firstRelation);
        CubeMultiRelation lastRelation = path.getLastRelation();
        RelationIndex lastIndex = third.getRelation(lastRelation);
        RelationColumn firstColumn = new RelationColumn(firstIndex, firstSegments, new ColumnKey("客户ID"));
        RelationColumn lastColumn = new RelationColumn(lastIndex, secondSegments, new ColumnKey("合同ID"));
        int pos = 1;
        for (int i = 0; i < firstRow; i++) {
            Object p1 = primary1.get(i);
            LongArray targetIndex = index.getIndex(pos++);
            for (int j = 0; j < targetIndex.size(); j++) {
                long value = targetIndex.get(j);
                if (NIOConstant.LONG.NULL_VALUE != value) {
                    int[] result = RelationIndexHelper.reverse2SegAndRow(value);
                    int[] secondInfo = lastColumn.getPrimarySegAndRow(result[1]);
                    Object p2 = firstColumn.getPrimaryValue(secondInfo[1]);
                    assertEquals(p1, p2);
                }
            }
        }
    }
}