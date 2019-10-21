package com.fr.swift.executor.task.job.impl;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SegmentUtils.class})
public class IndexJobTest {

    @Test
    public void call() throws Exception {
        SegmentKey segKey = mock(SegmentKey.class);

        mockStatic(SegmentUtils.class);
        Segment seg = mock(Segment.class);
        when(SegmentUtils.newSegment(segKey)).thenReturn(seg);

        IndexJob job = new IndexJob(segKey);

        assertTrue(job.call());
        verifyStatic(SegmentUtils.class);
        SegmentUtils.newSegment(segKey);
        verifyStatic(SegmentUtils.class);
        SegmentUtils.indexSegmentIfNeed(Collections.singletonList(seg));

        doThrow(new Exception()).when(SegmentUtils.class);
        SegmentUtils.indexSegmentIfNeed(Collections.singletonList(seg));

        assertFalse(job.call());
    }
}