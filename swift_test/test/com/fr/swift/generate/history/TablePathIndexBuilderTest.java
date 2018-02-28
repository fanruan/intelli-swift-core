package com.fr.swift.generate.history;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.cube.queue.CubeTasks;
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
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.relation.utils.MultiRelationHelper;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.IRelationSource;
import com.fr.swift.source.SourceKey;
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
 * @date 2018/2/8
 */
public class TablePathIndexBuilderTest extends TestCase {

    CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void setUp() {
        new LocalSwiftServerService().start();

        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
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
            } else if (o instanceof IRelationSource) {
                IRelationSource ds = ((IRelationSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new MultiRelationIndexBuilder(MultiRelationHelper.convert2CubeRelation(ds), LocalSegmentProvider.getInstance()));
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

        SchedulerTask customerTask = new SchedulerTaskImpl(new CubeTaskKey(customer.getMetadata().getTableName(), Operation.BUILD_TABLE));
        contractTask.addNext(customerTask);
        customerTask.addNext(end);
        l.add(new Pair<>(customerTask.key(), customer));
//        }

        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        IRelationSource relationSource = new RelationSource(dataSource.getSourceKey(), contract.getSourceKey(), primaryFields, foreignFields);
        CubeTaskKey key = new CubeTaskKey("relation", Operation.INDEX_RELATION);
        SchedulerTask task = new SchedulerTaskImpl(key);
        contractTask.addNext(task);
        task.addNext(end);
        l.add(new Pair<>(key, relationSource));


        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        IRelationSource custSource = new RelationSource(customer.getSourceKey(), dataSource.getSourceKey(), primaryFields1, foreignFields1);
        CubeTaskKey custKey = new CubeTaskKey("custRelation", Operation.INDEX_RELATION);
        SchedulerTask custTask = new SchedulerTaskImpl(custKey);
        customerTask.addNext(custTask);
        custTask.addNext(end);
        l.add(new Pair<>(custKey, custSource));
        CubeTasks.sendTasks(l);
        start.triggerRun();

        end.addStatusChangeListener((prev, now) -> {
            if (now == Task.Status.DONE) {
                latch.countDown();
            }
        });

        latch.await();
    }

    public void test() {
        List<String> primaryFields = new ArrayList<>();
        List<String> foreignFields = new ArrayList<>();
        primaryFields.add("合同ID");
        foreignFields.add("合同ID");
        IRelationSource relationSource = new RelationSource(new SourceKey("f58554da"), new SourceKey("57a0b2a5"), primaryFields, foreignFields);

        List<String> primaryFields1 = new ArrayList<>();
        List<String> foreignFields1 = new ArrayList<>();
        primaryFields1.add("客户ID");
        foreignFields1.add("客户ID");
        IRelationSource custSource = new RelationSource(new SourceKey("cf89ddaa"), new SourceKey("f58554da"), primaryFields1, foreignFields1);
        CubeMultiRelationPath path = new CubeMultiRelationPath();
        path.add(MultiRelationHelper.convert2CubeRelation(custSource));
        path.add(MultiRelationHelper.convert2CubeRelation(relationSource));
        TablePathIndexBuilder builder = new TablePathIndexBuilder(path, LocalSegmentProvider.getInstance());
        builder.work();
    }
}