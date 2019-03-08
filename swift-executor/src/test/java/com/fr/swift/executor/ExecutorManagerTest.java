package com.fr.swift.executor;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
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

import java.util.Collections;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({MemoryQueue.class, TaskRouter.class, SwiftContext.class})
public class ExecutorManagerTest {
    @Mock
    MemoryQueue memoryQueue;
    @Mock
    TaskRouter taskRouter;

    @Mock
    ExecutorTask executorTask1;
    @Mock
    ExecutorTask executorTask2;
    @Mock
    ExecutorTaskService executorTaskService;
    @Mock
    BeanFactory beanFactory;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(ExecutorTaskService.class)).thenReturn(executorTaskService);

        PowerMockito.mockStatic(MemoryQueue.class, TaskRouter.class);
        Mockito.when(MemoryQueue.getInstance()).thenReturn(memoryQueue);
        Mockito.when(TaskRouter.getInstance()).thenReturn(taskRouter);
    }

    @Test
    public void testPullEmpty() {
        Assert.assertFalse(ExecutorManager.getInstance().pull());
    }

    @Test
    public void testPullAll() {
        Mockito.when(DBQueue.getInstance().pullAll()).thenReturn(Collections.singletonList(executorTask1));
        Mockito.when(memoryQueue.pullBeforeTime(Mockito.anyLong())).thenReturn(Collections.singletonList(executorTask2));
        ExecutorManager.getInstance().pull();
        Mockito.verify(taskRouter).addTasks(Mockito.<ExecutorTask>anyList());
    }
}
