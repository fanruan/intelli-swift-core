package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.operator.Inserter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2018/8/23
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SegmentUtils.class})
public class SegmentTransferTest {
    @Mock
    private Inserter inserter;
    @Mock
    private SegmentKey oldSegKey;
    @Mock
    private SwiftSegmentService swiftSegmentService;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        Whitebox.setInternalState(SegmentTransfer.class, "SEG_SVC", swiftSegmentService);
        when(beanFactory.getBean(eq("inserter"), eq(Inserter.class), ArgumentMatchers.<Segment>any())).thenReturn(inserter);

        when(swiftSegmentService.containsSegment(oldSegKey)).thenReturn(true);

        mockStatic(SegmentUtils.class);
        when(SegmentUtils.newSegment(ArgumentMatchers.<SegmentKey>any())).thenReturn(mock(Segment.class));

        whenNew(SegmentResultSet.class).withAnyArguments().thenReturn(mock(SegmentResultSet.class));
    }

    @Test
    public void transfer() throws Exception {
        SegmentKey newSegKey = mock(SegmentKey.class);
        SegmentTransfer transfer = new SegmentTransfer(oldSegKey, newSegKey);

        transfer.transfer();

        // add seg conf
        verify(swiftSegmentService).addSegments(eq(Collections.singletonList(newSegKey)));

        // insert
        verify(inserter).insertData(ArgumentMatchers.<SwiftResultSet>any());

        // index
        verifyStatic(SegmentUtils.class);
        SegmentUtils.indexSegmentIfNeed(ArgumentMatchers.<List<Segment>>any());

        // remove old seg
        verify(swiftSegmentService).removeSegments(eq(Collections.singletonList(oldSegKey)));
        verifyStatic(SegmentUtils.class);
        SegmentUtils.clearSegment(oldSegKey);
    }

    @Test
    public void transferFailed() throws Exception {
        doThrow(Exception.class).when(inserter).insertData(ArgumentMatchers.<SwiftResultSet>any());

        SegmentKey newSegKey = mock(SegmentKey.class);
        SegmentTransfer transfer = new SegmentTransfer(oldSegKey, newSegKey);

        transfer.transfer();

        // add seg conf
        verify(swiftSegmentService).addSegments(eq(Collections.singletonList(newSegKey)));

        // insert failed
        verify(inserter).insertData(ArgumentMatchers.<SwiftResultSet>any());

        // won't index
        verifyStatic(SegmentUtils.class, times(0));
        SegmentUtils.indexSegmentIfNeed(ArgumentMatchers.<List<Segment>>any());

        // remove dirty seg
        verify(swiftSegmentService).removeSegments(eq(Collections.singletonList(newSegKey)));
        verifyStatic(SegmentUtils.class);
        SegmentUtils.clearSegment(newSegKey);
    }

    @Test
    public void transferNothing() throws Exception {
        when(swiftSegmentService.containsSegment(oldSegKey)).thenReturn(false);

        SegmentKey newSegKey = mock(SegmentKey.class);
        SegmentTransfer transfer = new SegmentTransfer(oldSegKey, newSegKey);

        transfer.transfer();

        // won't add seg conf, insert
        verify(swiftSegmentService).containsSegment(oldSegKey);
        verifyNoMoreInteractions(swiftSegmentService);
        verifyZeroInteractions(inserter);

        // won't index
        verifyStatic(SegmentUtils.class, times(0));
        SegmentUtils.indexSegmentIfNeed(ArgumentMatchers.<List<Segment>>any());

        // won't remove old seg
        verifyStatic(SegmentUtils.class, times(0));
        SegmentUtils.clearSegment(oldSegKey);
    }
}