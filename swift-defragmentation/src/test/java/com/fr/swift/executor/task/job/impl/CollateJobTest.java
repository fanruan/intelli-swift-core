package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

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
public class CollateJobTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
    }

    @Test
    public void call() throws Exception {
        CollateService collateService = mock(CollateService.class);
        when(SwiftContext.get().getBean(CollateService.class)).thenReturn(collateService);

        List segKeys = mock(List.class);
        SourceKey tableKey = mock(SourceKey.class);
        CollateJob job = new CollateJob(tableKey, segKeys);

        assertTrue(job.call());

        verify(collateService).appointCollate(tableKey, segKeys);

        doThrow(Exception.class).when(collateService).appointCollate(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<SegmentKey>anyList());

        assertFalse(job.call());
    }
}