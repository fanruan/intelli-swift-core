package com.fr.swift.cloud.executor.task.job.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.service.DeleteService;
import com.fr.swift.cloud.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class DeleteJobTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
    }

    @Test
    public void call() throws Exception {
        DeleteService deleteService = mock(DeleteService.class);
        PowerMockito.when(SwiftContext.get().getBean(DeleteService.class)).thenReturn(deleteService);

        Where where = mock(Where.class);
        SourceKey tableKey = mock(SourceKey.class);
        DeleteJob job = new DeleteJob(tableKey, where);

        assertTrue(job.call());

        verify(deleteService).delete(tableKey, where);

        doThrow(Exception.class).when(deleteService).delete(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<Where>any());

        assertFalse(job.call());
    }
}