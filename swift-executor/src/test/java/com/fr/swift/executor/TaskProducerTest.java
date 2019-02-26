package com.fr.swift.executor;

import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
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

import static org.junit.Assert.assertTrue;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({DBQueue.class, MemoryQueue.class})
public class TaskProducerTest {

    @Mock
    ExecutorTask dbTask;
    @Mock
    ExecutorTask memTask;
    @Mock
    DBQueue dbQueue;
    @Mock
    MemoryQueue memoryQueue;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(DBQueue.class, MemoryQueue.class);
        Mockito.when(DBQueue.getInstance()).thenReturn(dbQueue);
        Mockito.when(MemoryQueue.getInstance()).thenReturn(memoryQueue);
        Mockito.when(dbTask.isPersistent()).thenReturn(true);
        Mockito.when(memTask.isPersistent()).thenReturn(false);
    }

    @Test
    public void testProduceTask() {
        try {
            TaskProducer.produceTask(dbTask);
            Mockito.verify(dbQueue).put(dbTask);
            TaskProducer.produceTask(memTask);
            Mockito.verify(memoryQueue).offer(memTask);
        } catch (SQLException e) {
            assertTrue(false);
        }
    }
}
