package com.fr.swift.executor.queue;

import com.fr.swift.executor.task.ExecutorTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class MemoryQueueTest {

    @Mock
    ExecutorTask task1;
    @Mock
    ExecutorTask task2;
    @Mock
    ExecutorTask task3;
    @Mock
    ExecutorTask task4;


    @Before
    public void setUp() throws Exception {
        Mockito.when(task1.getCreateTime()).thenReturn(System.currentTimeMillis());
        Mockito.when(task2.getCreateTime()).thenReturn(System.currentTimeMillis() + 1);
        Mockito.when(task3.getCreateTime()).thenReturn(System.currentTimeMillis() + 2);
        Mockito.when(task4.getCreateTime()).thenReturn(System.currentTimeMillis() + 1000000);
    }

    @Test
    public void testPullBeforeTime() {
        MemoryQueue.getInstance().offer(task1);
        MemoryQueue.getInstance().offer(task2);
        MemoryQueue.getInstance().offer(task3);
        MemoryQueue.getInstance().offer(task4);
        List<ExecutorTask> executorTaskList = MemoryQueue.getInstance().pullBeforeTime(System.currentTimeMillis() + 10);
        assertEquals(executorTaskList.size(), 3);
        assertTrue(executorTaskList.contains(task1));
        assertTrue(executorTaskList.contains(task2));
        assertTrue(executorTaskList.contains(task3));
        assertTrue(!executorTaskList.contains(task4));
        MemoryQueue.getInstance().clear();
        assertEquals(MemoryQueue.getInstance().pullBeforeTime(0).size(), 0);
    }
}
