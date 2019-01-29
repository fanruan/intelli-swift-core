package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.BaseAllotRule.AllotType;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/5
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class HistoryLineSourceAlloterTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftSegmentService swiftSegmentService = mock(SwiftSegmentService.class);
        when(beanFactory.getBean("segmentServiceProvider", SwiftSegmentService.class)).thenReturn(swiftSegmentService);

        when(swiftSegmentService.tryAppendSegment(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<StoreType>any())).thenAnswer(new Answer<SegmentKey>() {
            int order = 0;

            @Override
            public SegmentKey answer(InvocationOnMock invocation) throws Throwable {
                SourceKey tableKey = invocation.getArgument(0);
                StoreType storeType = invocation.getArgument(1);
                return new SegmentKeyBean(tableKey, order++, storeType, null);
            }
        });
    }

    @Test
    public void allot() {
        HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey("a"), new LineAllotRule(2));

        assertEquals(newSegInfo(0), alloter.allot(new LineRowInfo(0)));
        assertEquals(newSegInfo(0), alloter.allot(new LineRowInfo(1)));
        assertEquals(newSegInfo(1), alloter.allot(new LineRowInfo(2)));
        assertEquals(newSegInfo(1), alloter.allot(new LineRowInfo(3)));
        assertEquals(newSegInfo(2), alloter.allot(new LineRowInfo(4)));
    }

    private SegmentInfo newSegInfo(int order) {
        return new SwiftSegmentInfo(order, StoreType.FINE_IO);
    }

    @Test
    public void getAllotRule() {
        HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey("a"), new LineAllotRule(2));
        LineAllotRule allotRule = alloter.getAllotRule();

        assertEquals(AllotType.LINE, allotRule.getType());
        assertEquals(2, allotRule.getCapacity());
    }
}