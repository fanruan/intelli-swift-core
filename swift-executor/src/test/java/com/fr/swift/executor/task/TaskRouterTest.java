package com.fr.swift.executor.task;

import com.fr.swift.executor.queue.ConsumeQueue;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class TaskRouterTest {

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
    @Mock
    ExecutorTask task7;
    @Mock
    ExecutorTask task8;
    @Mock
    ExecutorTask task9;

    @Before
    public void setUp() throws Exception {
        setSourceKey();
        setExecutorType();
        setLockKey();
        setLockType();
        setPriority();
        setTaskContent();
        ConsumeQueue.getInstance().offer(task1);
        ConsumeQueue.getInstance().offer(task2);
        ConsumeQueue.getInstance().offer(task3);
        List<ExecutorTask> taskList = new ArrayList<>();
        taskList.add(task4);
        taskList.add(task5);
        taskList.add(task6);
        taskList.add(task7);
        taskList.add(task8);
        taskList.add(task9);
        TaskRouter.getInstance().addTasks(taskList);
    }

    @Test
    public void pickExecutorTask() {
        Lock lock = new ReentrantLock();
        ExecutorTask pickedTask;

        // 测表级锁: 7, 8, 9 都是表名不冲突的，但 8, 9 的优先级更高, 且 8 重复 1，所以选 9
        pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
        Assert.assertEquals(pickedTask.getSourceKey().toString(), "focus9");
        ConsumeQueue.getInstance().offer(pickedTask);

        // 测表级锁: 4, 5, 6 的表名都冲突了，只能选 7
        pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
        Assert.assertEquals(pickedTask.getSourceKey().toString(), "focus7");
        ConsumeQueue.getInstance().offer(pickedTask);

        // 测信号量: 4 的类型 COLLATE 已经满了，只能选 6
        Mockito.when(task4.getSourceKey()).thenReturn(new SourceKey("focus4"));
        Mockito.when(task6.getSourceKey()).thenReturn(new SourceKey("focus6"));
        pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
        Assert.assertEquals(pickedTask.getSourceKey().toString(), "focus6");
        ConsumeQueue.getInstance().offer(pickedTask);

        // 测虚拟锁: 5 虽然是表名冲突，但是其是虚拟锁，选择
        Mockito.when(task5.getLockType()).thenReturn(LockType.VIRTUAL_SEG);
        pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
        Assert.assertEquals(pickedTask.getSourceKey().toString(), "focus_point");
        ConsumeQueue.getInstance().offer(pickedTask);

        // 测虚拟锁冲突: 已经添加了一个同 sourceKey 和 lockKey 的任务，只剩下一个冲突的任务，所以为 null
        Mockito.when(task4.getLockType()).thenReturn(LockType.VIRTUAL_SEG);
        Mockito.when(task4.getSourceKey()).thenReturn(new SourceKey("focus_point"));
        pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
        Assert.assertNull(pickedTask);
    }

    private void setSourceKey() {
        Mockito.when(task1.getSourceKey()).thenReturn(new SourceKey("focus_point"));
        Mockito.when(task2.getSourceKey()).thenReturn(new SourceKey("fo"));
        Mockito.when(task3.getSourceKey()).thenReturn(new SourceKey("foc"));
        Mockito.when(task4.getSourceKey()).thenReturn(new SourceKey("focus_point"));
        Mockito.when(task5.getSourceKey()).thenReturn(new SourceKey("focus_point"));
        Mockito.when(task6.getSourceKey()).thenReturn(new SourceKey("focus_point"));
        Mockito.when(task7.getSourceKey()).thenReturn(new SourceKey("focus7"));
        Mockito.when(task8.getSourceKey()).thenReturn(new SourceKey("focus8"));
        Mockito.when(task9.getSourceKey()).thenReturn(new SourceKey("focus9"));
    }

    private void setExecutorType() {
        Mockito.when(task1.getExecutorTaskType()).thenReturn(SwiftTaskType.COLLATE);
        Mockito.when(task2.getExecutorTaskType()).thenReturn(SwiftTaskType.HISTORY);
        Mockito.when(task3.getExecutorTaskType()).thenReturn(SwiftTaskType.REALTIME);
        Mockito.when(task4.getExecutorTaskType()).thenReturn(SwiftTaskType.COLLATE);
        Mockito.when(task5.getExecutorTaskType()).thenReturn(SwiftTaskType.INDEX);
        Mockito.when(task6.getExecutorTaskType()).thenReturn(SwiftTaskType.DOWNLOAD);
        Mockito.when(task7.getExecutorTaskType()).thenReturn(SwiftTaskType.TRANSFER);
        Mockito.when(task8.getExecutorTaskType()).thenReturn(SwiftTaskType.RECOVERY);
        Mockito.when(task9.getExecutorTaskType()).thenReturn(SwiftTaskType.UPLOAD);
    }

    private void setLockType() {
        Mockito.when(task1.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task2.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task3.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task4.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task5.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task6.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task7.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task8.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(task9.getLockType()).thenReturn(LockType.TABLE);
    }

    private void setLockKey() {
        Mockito.when(task1.getLockKey()).thenReturn("body1");
        Mockito.when(task2.getLockKey()).thenReturn("body2");
        Mockito.when(task3.getLockKey()).thenReturn("body3");
        Mockito.when(task4.getLockKey()).thenReturn("body5");
        Mockito.when(task5.getLockKey()).thenReturn("body5");
        Mockito.when(task6.getLockKey()).thenReturn("body6");
        Mockito.when(task7.getLockKey()).thenReturn("body7");
        Mockito.when(task8.getLockKey()).thenReturn("body8");
        Mockito.when(task9.getLockKey()).thenReturn("body9");
    }

    private void setPriority() {
        Mockito.when(task1.getPriority()).thenReturn(0);
        Mockito.when(task2.getPriority()).thenReturn(0);
        Mockito.when(task3.getPriority()).thenReturn(0);
        Mockito.when(task4.getPriority()).thenReturn(0);
        Mockito.when(task5.getPriority()).thenReturn(0);
        Mockito.when(task6.getPriority()).thenReturn(0);
        Mockito.when(task7.getPriority()).thenReturn(0);
        Mockito.when(task8.getPriority()).thenReturn(1);
        Mockito.when(task9.getPriority()).thenReturn(1);
    }

    private void setTaskContent() {
        Mockito.when(task1.getTaskContent()).thenReturn("{\"clientAppId\":\"test1\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task2.getTaskContent()).thenReturn("{\"clientAppId\":\"test2\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task3.getTaskContent()).thenReturn("{\"clientAppId\":\"test3\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task4.getTaskContent()).thenReturn("{\"clientAppId\":\"test4\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task5.getTaskContent()).thenReturn("{\"clientAppId\":\"test5\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task6.getTaskContent()).thenReturn("{\"clientAppId\":\"test6\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task7.getTaskContent()).thenReturn("{\"clientAppId\":\"test7\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task8.getTaskContent()).thenReturn("{\"clientAppId\":\"test1\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
        Mockito.when(task9.getTaskContent()).thenReturn("{\"clientAppId\":\"test9\",\"yearMonth\":\"202004\",\"version\":\"3.0\"}");
    }
}
