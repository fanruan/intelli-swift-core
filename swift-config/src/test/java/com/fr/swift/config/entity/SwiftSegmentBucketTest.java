package com.fr.swift.config.entity;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class SwiftSegmentBucketTest {

    @Test
    public void test() {
        SwiftSegmentBucket segmentBucket = new SwiftSegmentBucket(new SourceKey("test"));
        SegmentKey segmentKey = Mockito.mock(SegmentKey.class);
        SegmentKey segmentKey1 = Mockito.mock(SegmentKey.class);
        segmentBucket.put(0, segmentKey);
        segmentBucket.put(1, segmentKey1);
        Assert.assertEquals(0, segmentBucket.getBucketIndexMap().get(segmentKey).intValue());
        Assert.assertEquals(1, segmentBucket.getBucketIndexMap().get(segmentKey1).intValue());

        Assert.assertEquals(segmentKey, segmentBucket.getBucketMap().get(0).get(0));
        Assert.assertEquals(segmentKey1, segmentBucket.getBucketMap().get(1).get(0));
    }
}
