package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


/**
 * @author lucifer
 * @date 2020/4/9
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest(SegmentUtils.class)
public class LinePartitionerTest {

    List<SegmentKey> segmentList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        mockStatic(SegmentUtils.class);
        int count = 500000;
        for (int i = 0; i <= 10; i++) {
            SegmentKey segmentKey = mock(SegmentKey.class);
            Segment segment = mock(Segment.class);
            ImmutableBitMap allShow = mock(ImmutableBitMap.class);
            PowerMockito.doReturn(segment).when(SegmentUtils.class, "newSegment", segmentKey);
            when(segment.getAllShowIndex()).thenReturn(allShow);
            when(allShow.getCardinality()).thenReturn(count);
            count += 500000;
            segmentList.add(segmentKey);
        }
        for (int i = 0; i <= 5; i++) {
            SegmentKey segmentKey = mock(SegmentKey.class);
            Segment segment = mock(Segment.class);
            ImmutableBitMap allShow = mock(ImmutableBitMap.class);
            PowerMockito.doReturn(segment).when(SegmentUtils.class, "newSegment", segmentKey);
            when(segment.getAllShowIndex()).thenReturn(allShow);
            when(allShow.getCardinality()).thenReturn(10000);
            segmentList.add(segmentKey);
        }
    }

    @Test
    public void partition() {
        LinePartitioner partitioner = new LinePartitioner(10000000);
        List<SegmentPartition> segmentPartitions = partitioner.partition(segmentList);
        assertEquals(2, segmentPartitions.size());
        assertTrue(segmentPartitions.get(0).getSegments().size() >= 5);
        assertTrue(segmentPartitions.get(0).getCardinality() <= 10000000);

        assertTrue(segmentPartitions.get(1).getSegments().size() >= 2 && segmentPartitions.get(1).getSegments().size() <= 4);
        assertTrue(segmentPartitions.get(1).getCardinality() >= 8000000 && segmentPartitions.get(1).getCardinality() <= 10000000);
        return;
    }
}
