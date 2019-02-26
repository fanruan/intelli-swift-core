package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description 测试任务队列已满的情况
 */
public class ThreadIsBusyTest extends BaseDispatcherTest {


    @Mock
    ExecutorTask executorTask1;

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
        Mockito.verify(lock).lock();
        Mockito.verify(condition).await();
        Mockito.verify(lock).unlock();
    }
}
