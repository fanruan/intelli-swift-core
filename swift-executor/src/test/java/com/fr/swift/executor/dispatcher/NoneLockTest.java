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
public class NoneLockTest extends BaseDispatcherTest {

    @Mock
    ExecutorTask executorTaskNone;
    @Mock
    ExecutorTask executorTaskTableA;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(executorTaskNone.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskNone.getLockType()).thenReturn(LockType.NONE);

        Mockito.when(executorTaskTableA.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskTableA.getLockType()).thenReturn(LockType.TABLE);
    }

    @Test
    public void testNoneTask() throws InterruptedException {
        List<ExecutorTask> executorTaskList = new ArrayList<ExecutorTask>() {{
            add(executorTaskNone);
            add(executorTaskTableA);
            add(executorTaskNone);
        }};
        TaskRouter.getInstance().addTasks(executorTaskList);
        TaskDispatcher.getInstance();
        Thread.sleep(100L);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 3);
        //executorTaskNone none锁可以和任何任务同时执行
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(0), executorTaskNone);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(1), executorTaskTableA);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(2), executorTaskNone);


        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().size(), 0);
    }
}
