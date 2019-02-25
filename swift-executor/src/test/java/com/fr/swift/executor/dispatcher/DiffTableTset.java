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
 * @description 测试不同表任务直接执行
 */
public class DiffTableTset extends BaseDispatcherTest {


    @Mock
    ExecutorTask executorTaskA; // A TABLE
    @Mock
    ExecutorTask executorTaskB; // A TABLE

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(executorTaskA.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTaskB.getSourceKey()).thenReturn(new SourceKey("B"));
    }

    /**
     * @throws InterruptedException
     */
    @Test
    public void testDiffTable() throws InterruptedException {
        List<ExecutorTask> executorTaskList = new ArrayList<ExecutorTask>() {{
            add(executorTaskA);
            add(executorTaskB);
        }};
        TaskRouter.getInstance().addTasks(executorTaskList);
        TaskDispatcher.getInstance();
        Thread.sleep(100);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 2);
        //A,B不同表，同时执行
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(0), executorTaskA);
        Assert.assertEquals(ConsumeQueue.getInstance().getTaskList().get(1), executorTaskB);

        Assert.assertEquals(TaskRouter.getInstance().getIdleTasks().size(), 0);
    }
}
