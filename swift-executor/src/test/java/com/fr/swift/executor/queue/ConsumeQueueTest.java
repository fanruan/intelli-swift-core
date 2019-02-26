package com.fr.swift.executor.queue;

import com.fr.swift.executor.task.ExecutorTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;


/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class ConsumeQueueTest {
    @Mock
    ExecutorTask task1;
    @Mock
    ExecutorTask task2;
    @Mock
    ExecutorTask task3;
    @Mock
    ExecutorTask task4;

    List<ExecutorTask> taskList;

    @Before
    public void setUp() throws Exception {
        taskList = new ArrayList<ExecutorTask>() {{
            add(task1);
            add(task2);
            add(task3);
            add(task4);
        }};
    }

    @Test
    public void testOfferTakeAndRemove() throws InterruptedException {
        ConsumeQueue.getInstance().offer(task1);
        ConsumeQueue.getInstance().offer(task2);
        ConsumeQueue.getInstance().offer(task3);
        ConsumeQueue.getInstance().offer(task4);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 4);
        Assert.assertTrue(ConsumeQueue.getInstance().getTaskList().containsAll(taskList));

        ExecutorTask executorTask = ConsumeQueue.getInstance().take();
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 4);
        Assert.assertTrue(ConsumeQueue.getInstance().getTaskList().containsAll(taskList));

        Assert.assertFalse(ConsumeQueue.getInstance().removeTask(null));
        Assert.assertTrue(ConsumeQueue.getInstance().removeTask(executorTask));
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 3);
        taskList.remove(executorTask);
        Assert.assertTrue(ConsumeQueue.getInstance().getTaskList().containsAll(taskList));

    }
}
