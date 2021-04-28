package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.SwiftMetaData;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author lucifer
 * @date 2019-07-31
 * @description
 * @since advanced swift 1.0
 */
public class MutableCacheColumnSegmentTest {

    @Test
    public void test() {
        IResourceLocation location = Mockito.mock(IResourceLocation.class);
        SwiftMetaData meta = Mockito.mock(SwiftMetaData.class);
        MutableCacheColumnSegment segment = new MutableCacheColumnSegment(location, meta);
        Assert.assertEquals(segment.getMetaData(), meta);

        SwiftMetaData refreshedMeta = Mockito.mock(SwiftMetaData.class);
        segment.refreshMetadata(refreshedMeta);
        Assert.assertNotEquals(segment.getMetaData(), meta);
        Assert.assertEquals(segment.getMetaData(), refreshedMeta);

    }
}