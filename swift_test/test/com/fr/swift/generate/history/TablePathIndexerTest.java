package com.fr.swift.generate.history;

//import com.fr.dav.LocalEnv;

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
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.index.TablePathIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.relation.utils.MultiRelationHelper;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/2/8
 */
public class TablePathIndexerTest extends TestCase {

    CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void setUp() {
        new LocalSwiftServerService().start();
        // fixme LocalEnv没啦，配置写不进去，test不成功
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
        TestConnectionProvider.createConnection();
    }

    public void testWork() throws Exception {
        DataSource dataSource = new TableDBSource("DEMO_CONTRACT", "allTest");
        DataSource contract = new TableDBSource("DEMO_CAPITAL_RETURN", "allTest");
        DataSource customer = new TableDBSource("DEMO_CUSTOMER", "allTest");

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
            } else if (o instanceof RelationSource) {
                RelationSource ds = ((RelationSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new MultiRelationIndexer(MultiRelationHelper.convert2CubeRelation(ds), LocalSegmentProvider.getInstance()));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance().initListener();

        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();
        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

        SchedulerTask dataSourceTask = CubeTasks.newTableTask(dataSource);
        start.addNext(dataSourceTask);
        l.add(new Pair<>(dataSourceTask.key(), dataSource));


        SchedulerTask contractTask = CubeTasks.newTableTask(contract);
        start.addNext(contractTask);
        l.add(new Pair<>(contractTask.key(), contract));

        SchedulerTask customerTask = CubeTasks.newTableTask(customer);
        start.addNext(customerTask);
        l.add(new Pair<>(customerTask.key(), customer));
//        }

        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        RelationSource relationSource = new RelationSourceImpl(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
        SchedulerTask task = CubeTasks.newRelationTask(relationSource);
        dataSourceTask.addNext(task);
        contractTask.addNext(task);
        task.addNext(end);
        l.add(new Pair<>(task.key(), relationSource));


        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        RelationSource custSource = new RelationSourceImpl(customer.getSourceKey(), dataSource.getSourceKey(), primaryFields1, foreignFields1);
        SchedulerTask custTask = CubeTasks.newRelationTask(relationSource);
        customerTask.addNext(custTask);
        dataSourceTask.addNext(custTask);
        custTask.addNext(end);
        l.add(new Pair<>(custTask.key(), custSource));

        end.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });

        CubeTasks.sendTasks(l);
        start.triggerRun();

        latch.await();
    }

    public void test() {
        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        RelationSource relationSource = new RelationSourceImpl(new SourceKey("f58554da"), new SourceKey("57a0b2a5"), primaryFields, foreignFields);

        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        RelationSource custSource = new RelationSourceImpl(new SourceKey("cf89ddaa"), new SourceKey("f58554da"), primaryFields1, foreignFields1);
        CubeMultiRelationPath path = new CubeMultiRelationPath();
        path.add(MultiRelationHelper.convert2CubeRelation(custSource));
        path.add(MultiRelationHelper.convert2CubeRelation(relationSource));
        TablePathIndexer builder = new TablePathIndexer(path, LocalSegmentProvider.getInstance());
        builder.work();
    }
}