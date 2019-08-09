package com.fr.swift.executor.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.SwiftConfigQueryBus;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/3/1
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class ExecutorTaskServiceTest {

    @Mock
    BeanFactory beanFactory;
    @Mock
    SwiftConfig swiftConfig;

    @Mock
    SwiftConfigCommandBus commandBus;

    @Mock
    SwiftConfigQueryBus queryBus;

    @Mock
    ExecutorTask executorTask;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(SwiftConfig.class)).thenReturn(swiftConfig);
        Mockito.when(swiftConfig.query(SwiftExecutorTaskEntity.class)).thenReturn(queryBus);
        Mockito.when(swiftConfig.command(SwiftExecutorTaskEntity.class)).thenReturn(commandBus);
    }

    @Test
    public void testSaveOrUpdate() throws SQLException {
        new ExecutorTaskServiceImpl().saveOrUpdate(executorTask);
        Mockito.verify(commandBus).merge(executorTask.convert());
    }

    @Test
    public void testBatchSaveOrUpdate() throws SQLException {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>() {{
            add(Mockito.mock(ExecutorTask.class));
            add(Mockito.mock(ExecutorTask.class));
        }};
        new ExecutorTaskServiceImpl().batchSaveOrUpdate(executorTasks);
        Mockito.verify(commandBus, Mockito.times(2)).merge(Mockito.any(SwiftExecutorTaskEntity.class));
    }

    @Test
    public void getActiveTasksBeforeTime() {
        Mockito.when(queryBus.get(Mockito.any(SwiftConfigQuery.class))).thenReturn(new ArrayList());
        List<ExecutorTask> taskList = new ExecutorTaskServiceImpl().getActiveTasksBeforeTime(0);
        Mockito.verify(queryBus).get(Mockito.any(SwiftConfigQuery.class));
    }

    @Test
    public void deleteTask() throws SQLException {
        new ExecutorTaskServiceImpl().deleteTask(executorTask);
        Mockito.verify(commandBus).delete(Mockito.any(SwiftConfigCondition.class));
    }

    @Test
    public void getExecutorTask() throws SQLException {
        Mockito.when(queryBus.select(Mockito.any(String.class))).thenReturn(Collections.<SwiftExecutorTaskEntity>emptyList());
        Assert.assertEquals(new ExecutorTaskServiceImpl().getExecutorTask("testTask"), null);
        Mockito.verify(queryBus).select(Mockito.any(String.class));
    }
}