package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.source.SourceKey;
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
 * @description
 */
public class RealSegLockTest extends BaseDispatcherTest {

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
        Mockito.when(executorTaskAseg0.getLockKey()).thenReturn("seg0");

        Mockito.when(executorTaskAseg1.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAseg1.getLockType()).thenReturn(LockType.REAL_SEG);
        Mockito.when(executorTaskAseg1.getLockKey()).thenReturn("seg1");

        Mockito.when(executorTaskAvirtual.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskAvirtual.getLockType()).thenReturn(LockType.VIRTUAL_SEG);
    }

    @Test
    public void testRealSegLock() throws InterruptedException {
        List<ExecutorTask> executorTaskList = new ArrayList<ExecutorTask>() {{
            add(executorTaskAseg0);
            add(executorTaskAnone);
            add(executorTaskAtable);
            add(executorTaskAseg0);
            add(executorTaskAseg1);
            add(executorTaskAvirtual);
        }};
        TaskRouter.getInstance().addTasks(executorTaskList);
        TaskDispatcher.getInstance();
        Thread.sleep(100L);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 4);
        //seg0,none,seg1,virtual可同时i执行
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(0), executorTaskAseg0);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(1), executorTaskAnone);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(2), executorTaskAseg1);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(3), executorTaskAvirtual);

        //seg0和seg0/table不可同时执行
        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().size(), 2);
//        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().get(0), executorTaskAtable);
//        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().get(1), executorTaskAseg0);
    }
}
