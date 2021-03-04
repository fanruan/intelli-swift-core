package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.boot.service.SwiftServiceContext;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.executor.TaskProducer;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.task.impl.CollateExecutorTask;
import com.fr.swift.cloud.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.cloud.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.cloud.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, TaskProducer.class, SwiftServiceContext.class, SwiftDatabase.class
        , TruncateExecutorTask.class, SwiftServiceContext.class})
public class SwiftServiceContextTest {

    @Mock
    BeanFactory beanFactory;
    @Mock
    AnalyseService analyseService;
    @Mock
    HistoryService historyService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class, TaskProducer.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(AnalyseService.class)).thenReturn(analyseService);
        Mockito.when(beanFactory.getBean(HistoryService.class)).thenReturn(historyService);
        when(beanFactory.getBean(SwiftSegmentService.class)).thenReturn(mock(SwiftSegmentService.class));

        mockStatic(TaskProducer.class);
        when(TaskProducer.produceTask(ArgumentMatchers.<ExecutorTask>any())).thenReturn(true);
    }

    @Test
    public void getQueryResult() throws Exception {
        String queryJson = "queryJson";
        new SwiftServiceContext().getQueryResult(queryJson);
        Mockito.verify(analyseService).getQueryResult(queryJson);
    }

    @Test
    public void appointCollate() throws Exception {
        PowerMockito.mockStatic(SwiftServiceContext.class);
        PowerMockito.whenNew(CollateExecutorTask.class).withAnyArguments().thenReturn(Mockito.mock(CollateExecutorTask.class));

        new SwiftServiceContext().appointCollate(Mockito.mock(SourceKey.class), Mockito.mock(List.class));
        PowerMockito.verifyStatic(TaskProducer.class);
        TaskProducer.produceTask(any(CollateExecutorTask.class));
    }

    @Test
    public void delete() throws Exception {
        new SwiftServiceContext().delete(Mockito.mock(SourceKey.class), Mockito.mock(Where.class));
        PowerMockito.verifyStatic(TaskProducer.class);
        TaskProducer.produceTask(any(DeleteExecutorTask.class));
    }

}
