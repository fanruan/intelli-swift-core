package com.fr.swift.cloud.executor.dispatcher;

import com.fr.swift.cloud.executor.queue.ConsumeQueue;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.task.TaskRouter;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description 测试同一张表下，表锁任务和其他任务
 */
public class TableLockTest extends BaseDispatcherTest {

    @Mock
    ExecutorTask executorTaskAnone;
    @Mock
    ExecutorTask executorTaskAtable;
    @Mock
    ExecutorTask executorTaskAseg0;
    @Mock
    ExecutorTask executorTaskAseg1;
    @Mock
    ExecutorTask executorTaskAvirtual;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(executorTaskAnone.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAnone.getLockType()).thenReturn(LockType.NONE);

        Mockito.when(executorTaskAtable.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAtable.getLockType()).thenReturn(LockType.TABLE);

        Mockito.when(executorTaskAseg0.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAseg0.getLockType()).thenReturn(LockType.REAL_SEG);

        Mockito.when(executorTaskAseg1.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAseg1.getLockType()).thenReturn(LockType.REAL_SEG);

        Mockito.when(executorTaskAvirtual.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAvirtual.getLockType()).thenReturn(LockType.VIRTUAL_SEG);
    }

    @Test
    public void testTableLock() throws InterruptedException {
        List<ExecutorTask> executorTaskList = new ArrayList<ExecutorTask>() {{
            add(executorTaskAtable);
            add(executorTaskAnone);
            add(executorTaskAtable);
            add(executorTaskAseg0);
            add(executorTaskAseg1);
            add(executorTaskAvirtual);
        }};
        TaskRouter.getInstance().addTasks(executorTaskList);
        TaskDispatcher.getInstance();
        Thread.sleep(100L);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 3);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(0), executorTaskAtable);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(1), executorTaskAnone);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(2), executorTaskAvirtual);

        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().size(), 3);
//        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().get(0), executorTaskAtable);
//        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().get(1), executorTaskAseg0);
//        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().get(2), executorTaskAseg1);
    }
}
