package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.executor.task.job.Job.JobListener;
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
public class UploadJobTest {

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
        UploadJob job = new UploadJob(segKey, true, mock(JobListener.class));
        // 上传整个seg
        assertTrue(job.call());
        verify(uploadService).upload(Collections.singleton(segKey));

        job = new UploadJob(segKey, false, mock(JobListener.class));
        // 上传seg all show index
        assertTrue(job.call());
        verify(uploadService).uploadAllShow(Collections.singleton(segKey));

        doThrow(Exception.class).when(uploadService).uploadAllShow(ArgumentMatchers.<SegmentKey>anySet());
        // 出错
        assertFalse(job.call());
    }
}