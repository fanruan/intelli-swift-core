package com.fr.swift.generate.trans;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.BaseConfigTest;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author anchore
 * @date 2018/1/9
 */
public class TransAndIndexTest extends BaseConfigTest {
    CountDownLatch latch = new CountDownLatch(1);
    @Override
    protected void setUp() throws Exception{
        super.setUp();
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
        TestConfDb.setConfDb();
    }

    /**
     * 全量更新取数索引
     *
     * @throws Exception
     */
    public void testTransport() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");

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

        List<DataSource> dataSources = new ArrayList<DataSource>();
        dataSources.add(dataSource);
        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();

        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

        for (DataSource updateDataSource : dataSources) {
            SchedulerTask task = CubeTasks.newTableTask(updateDataSource);
            start.addNext(task);
            task.addNext(end);

            l.add(new Pair<>(task.key(), dataSource));
        }
        end.addStatusChangeListener((prev, now) -> {
            if (now == Status.DONE) {
                latch.countDown();
            }
        });

        CubeTasks.sendTasks(l);
        start.triggerRun();

        latch.await();

        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 1);
        Segment segment = segmentList.get(0);
        assertTrue(segment instanceof HistorySegmentImpl);
        assertEquals(segment.getRowCount(), 682);
        assertTrue(segment.getAllShowIndex().contains(0));
        assertTrue(segment.getAllShowIndex().contains(681));
        assertFalse(segment.getAllShowIndex().contains(682));
        try {
            for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
                String columnName = dataSource.getMetadata().getColumnName(i);
                Column column = segment.getColumn(new ColumnKey(columnName));
                assertNotNull(column.getBitmapIndex().getBitMapIndex(1));
                assertNotNull(column.getDictionaryEncodedColumn().getIndexByRow(1));
                assertNotNull(column.getDetailColumn().get(1));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}