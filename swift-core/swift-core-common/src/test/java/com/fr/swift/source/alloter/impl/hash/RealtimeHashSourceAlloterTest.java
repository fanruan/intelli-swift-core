package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class RealtimeHashSourceAlloterTest extends HistoryHashSourceAlloterTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testAllot() {
        RealtimeHashSourceAlloter alloter = new RealtimeHashSourceAlloter(new SourceKey("a"), new HashAllotRule());

        HashRowInfo hashRowInfo1 = new HashRowInfo(new ListBasedRow(0));
        HashRowInfo hashRowInfo2 = new HashRowInfo(new ListBasedRow(1));
        HashRowInfo hashRowInfo3 = new HashRowInfo(new ListBasedRow(2));
        HashRowInfo hashRowInfo4 = new HashRowInfo(new ListBasedRow(3));
        HashRowInfo hashRowInfo5 = new HashRowInfo(new ListBasedRow(4));
        Assert.assertEquals(newSegInfo(0), alloter.allot(hashRowInfo1));
        Assert.assertEquals(newSegInfo(1), alloter.allot(hashRowInfo2));
        Assert.assertEquals(newSegInfo(2), alloter.allot(hashRowInfo3));
        Assert.assertEquals(newSegInfo(3), alloter.allot(hashRowInfo4));
        Assert.assertEquals(newSegInfo(4), alloter.allot(hashRowInfo5));
        Mockito.verify(bucketService, Mockito.times(5)).saveElement(Mockito.any(SwiftSegmentBucketElement.class));
    }
}
