package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.ExecutorManager;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description 测试初始化没有任务时
 */
public class InitAndNullTest extends BaseDispatcherTest {

    /**
     * 初始化内存没任务，db memory队列均没任务
     * condition不会阻塞，pull为空，进入sleep
     *
     * @throws InterruptedException
     */
    @Test
    public void testWhenInitAndNull() throws Exception {
        PowerMockito.mockStatic(ExecutorManager.class);
        ExecutorManager executorManager = Mockito.mock(ExecutorManager.class);
        Mockito.when(ExecutorManager.getInstance()).thenReturn(executorManager);
        Mockito.when(executorManager.pullMemTask()).thenReturn(false);
        TaskDispatcher.getInstance();
        Thread.sleep(100);
        Mockito.verify(executorManager).pullMemTask();
    }
}