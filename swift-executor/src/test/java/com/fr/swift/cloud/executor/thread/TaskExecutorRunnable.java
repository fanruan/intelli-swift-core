package com.fr.swift.cloud.executor.thread;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.executor.config.ExecutorTaskService;
import com.fr.swift.cloud.executor.queue.ConsumeQueue;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.task.job.ExecutorJob;
import com.fr.swift.cloud.executor.task.job.Job;
import com.fr.swift.cloud.executor.task.job.Job.JobListener;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
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
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({TaskExecuteRunnable.class, SwiftContext.class})
public class TaskExecutorRunnable {

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

    @Mock
    ExecutorTaskService executorTaskService;

    @Mock
    BeanFactory beanFactory;


    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(ExecutorTaskService.class)).thenReturn(executorTaskService);

        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        taskExecuteRunnable = new TaskExecuteRunnable(dispatchThread, "testThread", lock, condition, SwiftTaskType.DELETE, SwiftTaskType.REALTIME, SwiftTaskType.COLLATE);
        PowerMockito.whenNew(ExecutorJob.class).withAnyArguments().thenReturn(executorJob);
        Mockito.when(task1.getJob()).thenReturn(job);
        Mockito.when(job.getJobListener()).thenReturn(jobListener);
    }

    @Test
    public void testRun() throws InterruptedException, ExecutionException {
        ConsumeQueue.getInstance().offer(task1);
        Assert.assertNull(taskExecuteRunnable.getExecutorTask());
        Assert.assertEquals(taskExecuteRunnable.getThreadName(), "testThread");
        Assert.assertTrue(taskExecuteRunnable.isIdle());
        new Thread(() -> taskExecuteRunnable.run()).start();
        Thread.sleep(100l);
        Mockito.verify(executorJob).run();
        Mockito.verify(executorJob).get();
        executorTaskService.delete(task1);
        Mockito.verify(jobListener).onDone(true);
        Assert.assertEquals(ConsumeQueue.getInstance().size(), 0);
    }

    @Test
    public void testMatch() {
        Assert.assertTrue(taskExecuteRunnable.isMatch(SwiftTaskType.COLLATE));
        Assert.assertTrue(taskExecuteRunnable.isMatch(SwiftTaskType.DELETE));
        Assert.assertTrue(taskExecuteRunnable.isMatch(SwiftTaskType.REALTIME));

        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.INDEX));
        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.HISTORY));
        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.TRANSFER));
        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.DOWNLOAD));
        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.UPLOAD));
        Assert.assertFalse(taskExecuteRunnable.isMatch(SwiftTaskType.QUERY));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(taskExecuteRunnable.toString(), "TaskExecuteRunnable{threadName='testThread', threadExecutorTypes=[DELETE][REALTIME][COLLATE]}");
    }
}
