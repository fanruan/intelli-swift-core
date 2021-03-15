package com.fr.swift.cloud.executor.queue;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.executor.config.ExecutorTaskService;
import com.fr.swift.cloud.executor.task.ExecutorTask;
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
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class DBQueueTest {
    @Mock
    ExecutorTaskService executorTaskService;

    @Mock
    ExecutorTask executorTask;

    @Mock
    BeanFactory beanFactory;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(ExecutorTaskService.class)).thenReturn(executorTaskService);
        Mockito.when(executorTaskService.getActiveTasksBeforeTime(0)).thenReturn(Collections.singletonList(executorTask));

    }

    @Test
    public void testputAndPull() throws SQLException {
        DBQueue.getInstance().put(executorTask);
        Mockito.verify(executorTaskService).save(executorTask);

        List<ExecutorTask> list = DBQueue.getInstance().pullAll();
        Assert.assertEquals(list.size(), 1);
    }
}
