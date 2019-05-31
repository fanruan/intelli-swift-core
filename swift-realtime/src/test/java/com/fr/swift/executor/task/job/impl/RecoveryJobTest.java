package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.recover.SegmentRecovery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

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
public class RecoveryJobTest {

    @Test
    public void call() {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        SegmentRecovery segmentRecovery = mock(SegmentRecovery.class);
        when(SwiftContext.get().getBean(SegmentRecovery.class)).thenReturn(segmentRecovery);

        SegmentKey segKey = mock(SegmentKey.class);
        new RecoveryJob(segKey).call();

        verify(segmentRecovery).recover(Collections.singletonList(segKey));
    }
}