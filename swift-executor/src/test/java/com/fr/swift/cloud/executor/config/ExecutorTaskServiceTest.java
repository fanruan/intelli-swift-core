package com.fr.swift.cloud.executor.config;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.executor.task.ExecutorTask;
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
    ExecutorTask executorTask;


    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);

    }

    @Test
    public void testSaveOrUpdate() throws SQLException {
//        new ExecutorTaskServiceImpl().save(executorTask);
    }

    @Test
    public void testBatchSaveOrUpdate() throws SQLException {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>() {{
            add(Mockito.mock(ExecutorTask.class));
            add(Mockito.mock(ExecutorTask.class));
        }};
//        new ExecutorTaskServiceImpl().batchSaveOrUpdate(executorTasks);
//        Mockito.verify(executorTaskDao, Mockito.times(2)).saveOrUpdate(Mockito.eq(configSession), Mockito.any(SwiftExecutorTaskEntity.class));
    }

    @Test
    public void getActiveTasksBeforeTime() {
//        Mockito.when(executorTaskDao.find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any())).thenReturn(new ArrayList());
//        List<ExecutorTask> taskList = new ExecutorTaskServiceImpl().getActiveTasksBeforeTime(0);
//        Mockito.verify(executorTaskDao).find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any());
    }

    @Test
    public void getRemoteActiveTasksBeforeTime() {
//        Mockito.when(executorTaskDao.find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any())).thenReturn(new ArrayList());
//        List<ExecutorTask> taskList = new ExecutorTaskServiceImpl().getRemoteActiveTasksBeforeTime(0);
//        Mockito.verify(executorTaskDao).find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any());
    }

    @Test
    public void deleteTask() throws SQLException {
//        new ExecutorTaskServiceImpl().deleteTask(executorTask);
//        Mockito.verify(executorTaskDao).delete(configSession, (SwiftExecutorTaskEntity) executorTask.convert());
    }

    @Test
    public void getExecutorTask() throws SQLException {
//        Mockito.when(executorTaskDao.find(Mockito.eq(configSession), Mockito.<ConfigWhere[]>any())).thenReturn(Collections.<SwiftExecutorTaskEntity>emptyList());
//        Assert.assertEquals(new ExecutorTaskServiceImpl().getExecutorTask("testTask"), null);
//        Mockito.verify(executorTaskDao).find(Mockito.eq(configSession), Mockito.<ConfigWhere[]>any());
    }
}