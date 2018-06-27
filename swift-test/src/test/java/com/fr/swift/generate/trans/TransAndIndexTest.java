package com.fr.swift.generate.trans;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
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
import com.fr.swift.structure.Pair;
import com.fr.swift.test.Preparer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2018/1/9
 */
public class TransAndIndexTest extends BaseConfigTest {
    CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Preparer.prepareCubeBuild();
        new LocalSwiftServerService().start();
    }

    /**
     * 全量更新取数索引
     *
     * @throws Exception
     */
    @Ignore
    @Test(timeout = 1000 * 60 * 10)
    public void testTransport() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");

        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator((taskKey, taskInfo) -> {
            if (taskKey.operation() == Operation.NULL) {
                return new WorkerTaskImpl(taskKey, BaseWorker.nullWorker());
            }

            if (taskInfo instanceof DataSource) {
                DataSource ds = ((DataSource) taskInfo);
                return new WorkerTaskImpl(taskKey, new TableBuilder(ds));
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
            SchedulerTask task = new SchedulerTaskImpl(CubeTasks.newBuildTableTaskKey(updateDataSource));
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

        List<Segment> segmentList = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
        assertEquals(segmentList.size(), 1);
        Segment segment = segmentList.get(0);
        assertTrue(segment instanceof HistorySegmentImpl);
        assertEquals(segment.getRowCount(), 682);
        assertTrue(segment.getAllShowIndex().contains(0));
        assertTrue(segment.getAllShowIndex().contains(681));
        assertFalse(segment.getAllShowIndex().contains(682));

        for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
            String columnName = dataSource.getMetadata().getColumnName(i);
            Column column = segment.getColumn(new ColumnKey(columnName));
            assertNotNull(column.getBitmapIndex().getBitMapIndex(1));
            assertNotNull(column.getDictionaryEncodedColumn().getIndexByRow(1));
            assertNotNull(column.getDetailColumn().get(1));
        }
    }
}