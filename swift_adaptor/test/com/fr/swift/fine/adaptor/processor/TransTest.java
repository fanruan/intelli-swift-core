package com.fr.swift.fine.adaptor.processor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.connection.FineConnectionImp;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.adaptor.update.SwiftUpdateManager;
import com.fr.swift.cube.queue.StuffProviderQueue;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.resource.ResourceUtils;
import com.fr.swift.segment.Segment;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/9
 */
public class TransTest extends TestCase {


    @Override
    protected void setUp() throws Exception {
        new LocalSwiftServerService().start();

        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));

        IConnectionProvider connectionProvider = new ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        String path = ResourceUtils.getFileAbsolutePath("com/fr/swift/resource/h2");
        FineConnection connection = new FineConnectionImp("jdbc:h2://" + path + "/test", "sa", "", "org.h2.Driver", "TransTest", null, null, null);
        FineConnectionUtils.removeAllConnections();
        FineConnectionUtils.addNewConnection(connection);
    }

    public void testBuild() throws Exception {
        FineBusinessTable fineBusinessTable = new FineDBBusinessTable("DEMO_CAPITAL_RETURN1", FineEngineType.Cube, "TransTest", "DEMO_CAPITAL_RETURN");
        SwiftUpdateManager manager = new SwiftUpdateManager();
        manager.saveUpdateSetting(null, fineBusinessTable);

        IndexStuffProvider provider = StuffProviderQueue.getQueue().poll();

        SchedulerTaskPool.getInstance();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.key();
            if (taskKey.operation() == Operation.NULL) {
                return new WorkerTaskImpl(taskKey);
            }

            Object o = pair.value();
            if (o instanceof DataSource) {
                DataSource ds = ((DataSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new TableBuilder(ds));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance();

        List<DataSource> dataSources = provider.getAllTables();
        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        assert (dataSources.size() == 1);
        DataSource dbDataSource = dataSources.get(0);

        SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey("start all")),
                end = new SchedulerTaskImpl(new CubeTaskKey("end all"));

//        end.addStatusChangeListener(new TaskStatusChangeListener() {
//            @Override
//            public void onChange(Task.Status prev, Task.Status now) {
//                if (now == Task.Status.DONE) {
//                    assertTrue(false);
//                }
//            }
//        });

        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

        for (DataSource dataSource : dataSources) {
            SchedulerTask task = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
            start.addNext(task);
            task.addNext(end);

            l.add(new Pair<>(task.key(), dataSource));
        }

        SchedulerTaskPool.sendTasks(l);

        start.triggerRun();
        Thread.sleep(10000l);
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dbDataSource.getSourceKey());

        for (int i = 1; i <= dbDataSource.getMetadata().getColumnCount(); i++) {
            dbDataSource.getMetadata().getColumnName(i);
        }
    }
}
