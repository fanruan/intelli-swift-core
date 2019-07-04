package com.fr.swift.segment.collate;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class FragmentFilterTest {

    @Mock
    SwiftSegmentManager localSegments;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);
        when(SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(localSegments);
    }

    @Test
    public void testSwiftFragmentCollectRule() {
        SwiftSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey("test"), new LineAllotRule(100));
        SwiftFragmentFilter rule = new SwiftFragmentFilter(alloter);

        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        for (int i = 0; i < 10; i++) {
            SegmentKey segmentKey = mock(SegmentKey.class);
            Segment segment = mock(Segment.class);
            IResourceLocation location = mock(IResourceLocation.class);
            when(location.getStoreType()).thenReturn(Types.StoreType.FINE_IO);
            when(segment.getLocation()).thenReturn(location);
            when(segment.getRowCount()).thenReturn(10);
            when(localSegments.getSegment(segmentKey)).thenReturn(segment);
            segmentKeyList.add(segmentKey);
        }
        for (int i = 0; i < 10; i++) {
            SegmentKey segmentKey = mock(SegmentKey.class);
            Segment segment = mock(Segment.class);
            IResourceLocation location = mock(IResourceLocation.class);
            when(location.getStoreType()).thenReturn(Types.StoreType.FINE_IO);
            when(segment.getLocation()).thenReturn(location);
            when(segment.getRowCount()).thenReturn(100);
            ImmutableBitMap allShowIndex = mock(ImmutableBitMap.class);
            when(segment.getAllShowIndex()).thenReturn(allShowIndex);
            when(allShowIndex.isFull()).thenReturn(false);
            when(allShowIndex.getCardinality()).thenReturn(10 * i);
            when(localSegments.getSegment(segmentKey)).thenReturn(segment);
            segmentKeyList.add(segmentKey);
        }
        Assert.assertEquals(17, rule.filter(segmentKeyList).size());


        segmentKeyList = new ArrayList<SegmentKey>();
        for (int i = 0; i < 10; i++) {
            SegmentKey segmentKey = mock(SegmentKey.class);
            Segment segment = mock(Segment.class);
            IResourceLocation location = mock(IResourceLocation.class);
            when(location.getStoreType()).thenReturn(Types.StoreType.FINE_IO);
            when(segment.getLocation()).thenReturn(location);
            when(segment.getRowCount()).thenReturn(80);
            ImmutableBitMap allShowIndex = mock(ImmutableBitMap.class);
            when(segment.getAllShowIndex()).thenReturn(allShowIndex);
            when(allShowIndex.isFull()).thenReturn(true);
            when(localSegments.getSegment(segmentKey)).thenReturn(segment);
            segmentKeyList.add(segmentKey);
        }
        Assert.assertTrue(rule.filter(segmentKeyList).isEmpty());
    }
}
