package com.fr.swift.cloud.executor.dispatcher;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.executor.ExecutorManager;
import com.fr.swift.cloud.executor.config.ExecutorTaskService;
import com.fr.swift.cloud.executor.queue.ConsumeQueue;
import com.fr.swift.cloud.executor.thread.TaskExecuteRunnable;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * This class created on 2019/2/25
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({Runtime.class, TaskDispatcher.class, ExecutorManager.class, ConsumeQueue.class, SwiftContext.class})
public abstract class BaseDispatcherTest {
    @Mock
    TaskExecuteRunnable executeRunnable;
    @Mock
    Runtime runtime;

    @Mock
    ExecutorTaskService executorTaskService;

    @Mock
    BeanFactory beanFactory;

    @Mock
    ExecutorManager executorManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);

        PowerMockito.mockStatic(ExecutorManager.class);
        Mockito.when(ExecutorManager.getInstance()).thenReturn(executorManager);

        PowerMockito.mockStatic(Runtime.class);
        Mockito.when(Runtime.getRuntime()).thenReturn(runtime);
        Mockito.when(runtime.availableProcessors()).thenReturn(5);

        PowerMockito.whenNew(TaskExecuteRunnable.class).withAnyArguments().thenReturn(executeRunnable);
    }
}
