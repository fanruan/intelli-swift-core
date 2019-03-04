package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.UploadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
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
public class DownloadJobTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
    }

    @Test
    public void call() throws Exception {
        UploadService uploadService = mock(UploadService.class);
        when(SwiftContext.get().getBean(UploadService.class)).thenReturn(uploadService);

        SegmentKey segKey = mock(SegmentKey.class);
        DownloadJob job = new DownloadJob(segKey, true, true);

        assertTrue(job.call());
        verify(uploadService).download(Collections.singleton(segKey), true);

        doThrow(Exception.class).when(uploadService).download(ArgumentMatchers.<SegmentKey>anySet(), eq(true));

        assertFalse(job.call());

        new DownloadJob(segKey, false, false).call();

        verify(uploadService).downloadAllShow(Collections.singleton(segKey));

        new DownloadJob(segKey, false, true).call();

        verify(uploadService, times(2)).downloadAllShow(Collections.singleton(segKey));
    }
}