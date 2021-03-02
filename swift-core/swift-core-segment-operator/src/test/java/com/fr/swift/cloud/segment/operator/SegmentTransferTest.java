package com.fr.swift.cloud.segment.operator;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentResultSet;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.operator.collate.segment.SegmentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2018/8/23
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SegmentUtils.class, SegmentTransfer.class})
public class SegmentTransferTest {
    @Mock
    private SegmentKey oldSegKey;
    @Mock
    private SwiftSegmentService segmentService;
    @Mock
    private SegmentBuilder segmentBuilder;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        Whitebox.setInternalState(SegmentTransfer.class, "SEG_SVC", segmentService);

//        when(segmentService.containsSegment(oldSegKey)).thenReturn(true);

        PowerMockito.whenNew(SegmentResultSet.class).withAnyArguments().thenReturn(mock(SegmentResultSet.class));

        SegmentService segmentManager = mock(SegmentService.class);
        when(SwiftContext.get().getBean(SegmentService.class)).thenReturn(segmentManager);

        whenNew(SegmentBuilder.class).withAnyArguments().thenReturn(segmentBuilder);
    }

    @Test
    public void transfer() throws Exception {
        SegmentKey newSegKey = mock(SegmentKey.class);

        mockStatic(SegmentUtils.class);
        Segment oldSeg = mock(Segment.class, RETURNS_DEEP_STUBS), newSeg = mock(Segment.class);
        when(SegmentUtils.newSegment(oldSegKey)).thenReturn(oldSeg);
        when(SegmentUtils.newSegment(newSegKey)).thenReturn(newSeg);

        SegmentTransfer transfer = new SegmentTransfer(oldSegKey);
        transfer.transfer();

        // add seg conf
//        verify(segmentService).addSegments(eq(Collections.singletonList(newSegKey)));

        // build seg
        verifyNew(SegmentBuilder.class).withArguments(newSeg, oldSeg.getMetaData().getFieldNames(), Collections.singletonList(oldSeg), Collections.singletonList(oldSeg.getAllShowIndex()));
        verify(segmentBuilder).build();

        // remove old seg
//        verify(segmentService).removeSegments(eq(Collections.singletonList(oldSegKey)));
        verifyStatic(SegmentUtils.class);
        SegmentUtils.clearSegment(oldSegKey);
        verify(SwiftContext.get().getBean(SegmentService.class)).getSegment(newSegKey);

        // finally release his seg
        verifyStatic(SegmentUtils.class);
        SegmentUtils.releaseHisSeg(Arrays.asList(oldSeg, newSeg));
    }

    @Test
    public void transferFailed() throws Exception {
        SegmentKey newSegKey = mock(SegmentKey.class);

        mockStatic(SegmentUtils.class);
        Segment oldSeg = mock(Segment.class, RETURNS_DEEP_STUBS), newSeg = mock(Segment.class);
        when(SegmentUtils.newSegment(oldSegKey)).thenReturn(oldSeg);
        when(SegmentUtils.newSegment(newSegKey)).thenReturn(newSeg);

        doThrow(Exception.class).when(segmentBuilder).build();

        SegmentTransfer transfer = new SegmentTransfer(oldSegKey);
        transfer.transfer();

        // add seg conf
//        verify(segmentService).addSegments(eq(Collections.singletonList(newSegKey)));

        // build seg
        verifyNew(SegmentBuilder.class).withArguments(newSeg, oldSeg.getMetaData().getFieldNames(), Collections.singletonList(oldSeg), Collections.singletonList(oldSeg.getAllShowIndex()));
        verify(segmentBuilder).build();

        // remove dirty seg
//        verify(segmentService).removeSegments(eq(Collections.singletonList(newSegKey)));
        verifyStatic(SegmentUtils.class);
        SegmentUtils.clearSegment(newSegKey);

        // finally release his seg
        verifyStatic(SegmentUtils.class);
        SegmentUtils.releaseHisSeg(Arrays.asList(oldSeg, newSeg));
    }
}