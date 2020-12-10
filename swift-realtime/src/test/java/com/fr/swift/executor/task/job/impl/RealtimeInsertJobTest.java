package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.SourceKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class RealtimeInsertJobTest {

    @Test
    public void call() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        RealtimeService realtimeService = mock(RealtimeService.class);
        when(SwiftContext.get().getBean(RealtimeService.class)).thenReturn(realtimeService);

        SourceKey tableKey = mock(SourceKey.class);
        SwiftResultSet resultSet = mock(SwiftResultSet.class);
        RealtimeInsertJob job = new RealtimeInsertJob(tableKey, resultSet);

        assertNotNull(job.call());
        verify(realtimeService).insert(tableKey, resultSet);

        doThrow(new Exception()).when(realtimeService).insert(tableKey, resultSet);

        assertNull(job.call());
    }
}