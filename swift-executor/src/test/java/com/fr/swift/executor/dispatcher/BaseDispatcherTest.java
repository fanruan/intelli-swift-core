package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.thread.TaskExecuteRunnable;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({Runtime.class, TaskDispatcher.class, ExecutorManager.class, Runtime.class, ConsumeQueue.class})
public abstract class BaseDispatcherTest {
    @Mock
    TaskExecuteRunnable executeRunnable;
    @Mock
    Runtime runtime;
    @Mock
    ReentrantLock lock;
    @Mock
    Condition condition;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Runtime.class);
        Mockito.when(Runtime.getRuntime()).thenReturn(runtime);
        Mockito.when(runtime.availableProcessors()).thenReturn(5);

        PowerMockito.whenNew(ReentrantLock.class).withAnyArguments().thenReturn(lock);
        Mockito.when(lock.newCondition()).thenReturn(condition);

        PowerMockito.whenNew(TaskExecuteRunnable.class).withAnyArguments().thenReturn(executeRunnable);
    }
}
