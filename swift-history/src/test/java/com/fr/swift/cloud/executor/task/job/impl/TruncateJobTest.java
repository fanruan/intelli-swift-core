package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.service.HistoryService;
import com.fr.swift.cloud.service.RealtimeService;
import com.fr.swift.cloud.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class TruncateJobTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
    }

    @Test
    public void call() throws Exception {
        RealtimeService realtimeService = mock(RealtimeService.class);
        PowerMockito.when(SwiftContext.get().getBean(RealtimeService.class)).thenReturn(realtimeService);
        HistoryService historyService = mock(HistoryService.class);
        when(SwiftContext.get().getBean(HistoryService.class)).thenReturn(historyService);

        SourceKey tableKey = mock(SourceKey.class);
        TruncateJob job = new TruncateJob(tableKey);

        job.call();

        // 正常情况，按次序调用
        InOrder inOrder = inOrder(realtimeService, historyService);
        inOrder.verify(realtimeService).truncate(tableKey);
        inOrder.verify(historyService).truncate(tableKey);
        inOrder.verifyNoMoreInteractions();

        doThrow(Exception.class).when(realtimeService).truncate(ArgumentMatchers.<SourceKey>any());

        job.call();

        // 出错，也保证按次序调用
        inOrder = inOrder(realtimeService, historyService);
        inOrder.verify(realtimeService).truncate(tableKey);
        inOrder.verify(historyService).truncate(tableKey);
    }
}