package com.fr.swift.executor.task;

import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.source.SourceKey;
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
public class TaskRouterTest {

    long time = System.currentTimeMillis();
    @Mock
    Job job;
    @Mock
    ExecutorTask task1;
    @Mock
    ExecutorTask task2;
    @Mock
    ExecutorTask task3;
    @Mock
    ExecutorTask task4;
    @Mock
    ExecutorTask task5;
    @Mock
    ExecutorTask task6;
    ExecutorTask task7;

    @Before
    public void setUp() throws Exception {
        Mockito.when(task1.getCreateTime()).thenReturn(time);
        Mockito.when(task2.getCreateTime()).thenReturn(time + 1);
        Mockito.when(task3.getCreateTime()).thenReturn(time + 2);
        Mockito.when(task4.getCreateTime()).thenReturn(time + 3);
        Mockito.when(task5.getCreateTime()).thenReturn(time + 4);
        Mockito.when(task6.getCreateTime()).thenReturn(time + 5);
        task7 = new AbstractExecutorTask(new SourceKey("testA"), true, SwiftTaskType.COLLATE, LockType.TABLE, "testA", DBStatusType.ACTIVE, job) {
        };
    }

    @Test
    public void testAddTasksAndSort() {
        List<ExecutorTask> taskList1 = new ArrayList<ExecutorTask>() {{
            add(task3);
            add(task4);
            add(task1);
            add(task2);
        }};
        List<ExecutorTask> taskList2 = new ArrayList<ExecutorTask>() {{
            add(task7);
            add(task5);
            add(task6);
        }};
        TaskRouter.getInstance().addTasks(taskList1);
        List<ExecutorTask> idleTasks = TaskRouter.getInstance().getIdleTasks();
        Assert.assertEquals(idleTasks.size(), 4);
        Assert.assertEquals(idleTasks.get(0), task1);
        Assert.assertEquals(idleTasks.get(1), task2);
        Assert.assertEquals(idleTasks.get(2), task3);
        Assert.assertEquals(idleTasks.get(3), task4);

        TaskRouter.getInstance().addTasks(taskList2);
        Assert.assertEquals(idleTasks.size(), 7);
        Assert.assertEquals(idleTasks.get(0), task1);
        Assert.assertEquals(idleTasks.get(1), task2);
        Assert.assertEquals(idleTasks.get(2), task3);
        Assert.assertEquals(idleTasks.get(3), task4);
        Assert.assertEquals(idleTasks.get(4), task5);
        Assert.assertEquals(idleTasks.get(5), task6);
        Assert.assertEquals(idleTasks.get(6), task7);

        TaskRouter.getInstance().remove(task7);
        Assert.assertEquals(idleTasks.size(), 6);

        TaskRouter.getInstance().clear();
        Assert.assertEquals(idleTasks.size(), 0);
    }
}
