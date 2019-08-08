package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.transfer.SegmentTransfer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TransferJob.class, SwiftContext.class})
public class TransferJobTest {

    @Test
    public void call() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));

        SegmentTransfer transfer = mock(SegmentTransfer.class);
        whenNew(SegmentTransfer.class).withAnyArguments().thenReturn(transfer);

        SegmentKey segKey = mock(SegmentKey.class);
        TransferJob job = new TransferJob(segKey);

        assertTrue(job.call());
        verifyNew(SegmentTransfer.class).withArguments(segKey);
        verify(transfer).transfer();

        doThrow(new RuntimeException()).when(transfer).transfer();
        assertFalse(job.call());
    }
}