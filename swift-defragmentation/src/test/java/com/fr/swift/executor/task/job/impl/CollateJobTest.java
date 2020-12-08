package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.executor.task.bean.CollateBean;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.service.CollateService;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

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

    @Test(expected = Exception.class)
    public void call() throws Exception {
        CollateService collateService = mock(CollateService.class);
        SegmentService segmentService = mock(SegmentService.class);
        when(SwiftContext.get().getBean(CollateService.class)).thenReturn(collateService);
        when(SwiftContext.get().getBean(SegmentService.class)).thenReturn(segmentService);
        List segmentIds = mock(List.class);
        List segKeys = mock(List.class);
        SourceKey tableKey = mock(SourceKey.class);
        when(segmentService.getSegmentKeysByIds(tableKey, segmentIds)).thenReturn(segKeys);
        CollateJob job = new CollateJob(CollateBean.of(tableKey, segmentIds));
        assertTrue(job.call().isEmpty());
        verify(collateService).appointCollate(tableKey, segKeys);
        verify(segmentService).getSegmentKeysByIds(tableKey, segmentIds);

        doThrow(Exception.class).when(collateService).appointCollate(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<SegmentKey>anyList());
        job.call();
    }
}