package com.fr.swift.cloud.executor.dispatcher;

import com.fr.swift.cloud.executor.queue.ConsumeQueue;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description 测试任务队列已满的情况
 */
public class ThreadIsBusyTest extends BaseDispatcherTest {


    @Mock
    ExecutorTask executorTask1;
    @Mock
    ReentrantLock lock;
    @Mock
    Condition condition;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(lock.newCondition()).thenReturn(condition);
        PowerMockito.whenNew(ReentrantLock.class).withAnyArguments().thenReturn(lock);
        Mockito.when(condition.await(10000L, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());
    }

    /**
     * 线程队列满的情况下，会别await
     *
     * @throws InterruptedException
     */
    @Test
    public void testThreadIsBusy() throws Exception {
        ConsumeQueue.getInstance().offer(executorTask1);
        ConsumeQueue.getInstance().offer(executorTask1);
        ConsumeQueue.getInstance().offer(executorTask1);
        ConsumeQueue.getInstance().offer(executorTask1);
        ConsumeQueue.getInstance().offer(executorTask1);

        TaskDispatcher.getInstance();
        Thread.sleep(100L);
        Mockito.verify(lock, Mockito.times(2)).lock();
        Mockito.verify(condition).await();
        Mockito.verify(lock, Mockito.times(2)).unlock();
        Mockito.verify(condition).await(10000L, TimeUnit.MILLISECONDS);
    }
}
