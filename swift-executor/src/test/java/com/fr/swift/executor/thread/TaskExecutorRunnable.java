package com.fr.swift.executor.thread;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.job.ExecutorJob;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.Job.JobListener;
import com.fr.swift.executor.type.ExecutorTaskType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ConsumeQueue.class, TaskExecuteRunnable.class})
public class TaskExecutorRunnable {

    @Mock
    ConsumeQueue consumeQueue;
    @Mock
    Lock lock;
    @Mock
    Condition condition;
    @Mock
    ExecutorTask task1;
    @Mock
    Job job;
    @Mock
    ExecutorJob executorJob;

    @Mock
    Thread dispatchThread;

    TaskExecuteRunnable taskExecuteRunnable;

    @Mock
    private JobListener jobListener;


    @Before
    public void setUp() throws Exception {
        taskExecuteRunnable = new TaskExecuteRunnable(dispatchThread, "testThread", lock, condition, ExecutorTaskType.DELETE, ExecutorTaskType.REALTIME, ExecutorTaskType.COLLATE);
        PowerMockito.mockStatic(ConsumeQueue.class);
        PowerMockito.whenNew(ExecutorJob.class).withAnyArguments().thenReturn(executorJob);
        Mockito.when(task1.getJob()).thenReturn(job);
        Mockito.when(job.getJobListener()).thenReturn(jobListener);
        Mockito.when(ConsumeQueue.getInstance()).thenReturn(consumeQueue);
        Mockito.when(consumeQueue.take()).thenReturn(task1, null);
    }

    @Test
    public void testRun() throws InterruptedException, ExecutionException {
        Assert.assertNull(taskExecuteRunnable.getExecutorTask());
        Assert.assertEquals(taskExecuteRunnable.getThreadName(), "testThread");
        Assert.assertTrue(taskExecuteRunnable.isIdle());
        try {
            taskExecuteRunnable.run();
            Assert.assertTrue(false);
        } catch (NullPointerException e) {
            Mockito.verify(consumeQueue, Mockito.times(2)).take();
            Mockito.verify(executorJob).run();
            Mockito.verify(executorJob).get();
            Mockito.verify(jobListener).onDone(true);
            Mockito.verify(lock, Mockito.times(2)).lock();
            Mockito.verify(condition, Mockito.times(2)).signal();
            Mockito.verify(lock, Mockito.times(2)).unlock();
            Mockito.verify(dispatchThread, Mockito.times(2)).interrupt();
        }
    }

    @Test
    public void testMatch() {
        Assert.assertTrue(taskExecuteRunnable.isMatch(ExecutorTaskType.COLLATE));
        Assert.assertTrue(taskExecuteRunnable.isMatch(ExecutorTaskType.DELETE));
        Assert.assertTrue(taskExecuteRunnable.isMatch(ExecutorTaskType.REALTIME));

        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.INDEX));
        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.HISTORY));
        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.TRANSFER));
        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.DOWNLOAD));
        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.UPLOAD));
        Assert.assertFalse(taskExecuteRunnable.isMatch(ExecutorTaskType.QUERY));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(taskExecuteRunnable.toString(), "TaskExecuteRunnable{threadName='testThread', threadExecutorTypes=[DELETE][REALTIME][COLLATE]}");
    }
}
