package com.fr.swift.executor;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.exception.NotDBTaskExecption;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({MemoryQueue.class, SwiftContext.class})
public class TaskProducerTest {

    @Mock
    ExecutorTask dbTask;

    @Mock
    ExecutorTask memTask;

    @Mock
    MemoryQueue memoryQueue;

    @Mock
    ExecutorTaskService executorTaskService;

    @Mock
    BeanFactory beanFactory;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class, MemoryQueue.class);

        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(ExecutorTaskService.class)).thenReturn(executorTaskService);

        PowerMockito.mockStatic(MemoryQueue.class);
        Mockito.when(MemoryQueue.getInstance()).thenReturn(memoryQueue);

        Mockito.when(dbTask.isPersistent()).thenReturn(true);
        Mockito.when(memTask.isPersistent()).thenReturn(false);
    }

    @Test
    public void testProduceTask() throws SQLException {
        try {
            TaskProducer.produceTask(dbTask);
            Mockito.verify(executorTaskService).saveOrUpdate(dbTask);
            TaskProducer.produceTask(memTask);
            Mockito.verify(memoryQueue).offer(memTask);
        } catch (SQLException e) {
            assertTrue(false);
        }

        Set<ExecutorTask> executorTaskSet = new HashSet<ExecutorTask>() {{
            add(dbTask);
            add(memTask);
        }};

        try {
            TaskProducer.produceTasks(executorTaskSet);
            Assert.assertTrue(false);
        } catch (NotDBTaskExecption e) {
            Assert.assertTrue(true);
            Assert.assertEquals(e.getMessage(), "memTask");
        }

        Set<ExecutorTask> executorTaskSet2 = new HashSet<ExecutorTask>() {{
            add(dbTask);
        }};
        try {
            TaskProducer.produceTasks(executorTaskSet2);
            Mockito.verify(executorTaskService).batchSaveOrUpdate(executorTaskSet2);
            Assert.assertTrue(true);
        } catch (NotDBTaskExecption e) {
            Assert.assertTrue(false);
        }
    }

}
