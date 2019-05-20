package com.fr.swift.executor.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.executor.task.ExecutorTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.sql.SQLException;
import java.util.ArrayList;
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
    TransactionManager transactionManager;
    @Mock
    ExecutorTaskDao executorTaskDao;

    @Mock
    ExecutorTask executorTask;
    @Mock
    ConfigSession configSession;


    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(TransactionManager.class)).thenReturn(transactionManager);
        Mockito.when(beanFactory.getBean(ExecutorTaskDao.class)).thenReturn(executorTaskDao);

        Mockito.when(transactionManager.doTransactionIfNeed(Mockito.any(BaseTransactionWorker.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                TransactionManager.TransactionWorker worker = invocationOnMock.getArgument(0);
                return worker.work(configSession);
            }
        });
    }

    @Test
    public void testSaveOrUpdate() throws SQLException {
        new ExecutorTaskServiceImpl().saveOrUpdate(executorTask);
        Mockito.verify(executorTaskDao).saveOrUpdate(configSession, executorTask);
    }

    @Test
    public void testBatchSaveOrUpdate() throws SQLException {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>() {{
            add(Mockito.mock(ExecutorTask.class));
            add(Mockito.mock(ExecutorTask.class));
        }};
        new ExecutorTaskServiceImpl().batchSaveOrUpdate(executorTasks);
        Mockito.verify(executorTaskDao, Mockito.times(2)).saveOrUpdate(Mockito.eq(configSession), Mockito.any(ExecutorTask.class));
    }

    @Test
    public void getActiveTasksBeforeTime() {
        Mockito.when(executorTaskDao.find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any())).thenReturn(new FindListImpl<ExecutorTask>(new ArrayList()));
        List<ExecutorTask> taskList = new ExecutorTaskServiceImpl().getActiveTasksBeforeTime(0);
        Mockito.verify(executorTaskDao).find(Mockito.eq(configSession), Mockito.any(Order[].class), Mockito.<ConfigWhere[]>any());
    }

    @Test
    public void deleteTask() throws SQLException {
        new ExecutorTaskServiceImpl().deleteTask(executorTask);
        Mockito.verify(executorTaskDao).delete(configSession, executorTask);
    }

    @Test
    public void getExecutorTask() throws SQLException {
        Mockito.when(executorTaskDao.find(Mockito.eq(configSession), Mockito.<ConfigWhere[]>any())).thenReturn(new FindListImpl<ExecutorTask>(null));
        Assert.assertEquals(new ExecutorTaskServiceImpl().getExecutorTask("testTask"), null);
        Mockito.verify(executorTaskDao).find(Mockito.eq(configSession), Mockito.<ConfigWhere[]>any());
    }
}

