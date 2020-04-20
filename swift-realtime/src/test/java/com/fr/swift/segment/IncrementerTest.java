package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/11
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, Incrementer.class, SegmentUtils.class, SwiftEventDispatcher.class, BaseBlockImporter.class, SwiftInserter.class})
public class IncrementerTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        when(SwiftContext.get().getBean(SwiftTableAllotRuleService.class)).thenReturn(mock(SwiftTableAllotRuleService.class));
    }

    @Test
    public void getInserting() {
        SegmentKey segKey = mock(SegmentKey.class);
        Segment seg = mock(Segment.class);
        mockStatic(SegmentUtils.class);
        when(SegmentUtils.newSegment(segKey)).thenReturn(seg);

        SwiftInserter inserter = mock(SwiftInserter.class);
        mockStatic(SwiftInserter.class);
        when(SwiftInserter.ofAppendMode(seg)).thenReturn(inserter);

        when(seg.isReadable()).thenReturn(true);
        when(seg.getRowCount()).thenReturn(1);

        Incrementer<SwiftSourceAlloter<?, RowInfo>> incrementer = new Incrementer<SwiftSourceAlloter<?, RowInfo>>(mock(DataSource.class), mock(SwiftSourceAlloter.class));
        Object inserting = incrementer.getInserting(segKey);

        assertEquals(inserter, Whitebox.getInternalState(inserting, "inserter"));
        assertEquals(seg, Whitebox.getInternalState(inserting, "seg"));
        assertEquals(1, (int) Whitebox.getInternalState(inserting, "rowCount"));
    }

    @Test
    public void handleFullSegment() throws Exception {
        SegmentInfo segInfo = mock(SegmentInfo.class);
        Incrementer<SwiftSourceAlloter<?, RowInfo>> incrementer = spy(new Incrementer<SwiftSourceAlloter<?, RowInfo>>(mock(DataSource.class), mock(SwiftSourceAlloter.class)));

        SegmentKey segKey = mock(SegmentKey.class);
        doReturn(segKey).when(incrementer, "newSegmentKey", segInfo);

        mockStatic(SwiftEventDispatcher.class);

        incrementer.handleFullSegment(segInfo);

        verifyStatic(SwiftEventDispatcher.class);
        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
    }
}