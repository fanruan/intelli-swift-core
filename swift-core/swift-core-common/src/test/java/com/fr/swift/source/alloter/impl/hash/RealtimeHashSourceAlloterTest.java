package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.hash.function.TimeHashFunction;
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

        TimeHashFunction timeHashFunction = new TimeHashFunction();
        RealtimeHashSourceAlloter alloter2 = new RealtimeHashSourceAlloter(new SourceKey("b"), new HashAllotRule(0, timeHashFunction));

        HashRowInfo hashRowInfo1 = new HashRowInfo(new ListBasedRow(0));
        HashRowInfo hashRowInfo2 = new HashRowInfo(new ListBasedRow(1));
        HashRowInfo hashRowInfo3 = new HashRowInfo(new ListBasedRow(2));
        HashRowInfo hashRowInfo4 = new HashRowInfo(new ListBasedRow(3));
        HashRowInfo hashRowInfo5 = new HashRowInfo(new ListBasedRow(4));
        HashRowInfo hashRowInfo6 = new HashRowInfo(new ListBasedRow(1262361600000L));
        HashRowInfo hashRowInfo7 = new HashRowInfo(new ListBasedRow(1265040000000l));
        HashRowInfo hashRowInfo8 = new HashRowInfo(new ListBasedRow(1269187200000L));
        HashRowInfo hashRowInfo9 = new HashRowInfo(new ListBasedRow(1271692800000L));

        Assert.assertEquals(newSegInfo(0), alloter.allot(hashRowInfo1));
        Assert.assertEquals(newSegInfo(1), alloter.allot(hashRowInfo2));
        Assert.assertEquals(newSegInfo(2), alloter.allot(hashRowInfo3));
        Assert.assertEquals(newSegInfo(3), alloter.allot(hashRowInfo4));
        Assert.assertEquals(newSegInfo(4), alloter.allot(hashRowInfo5));
        Assert.assertEquals(newSegInfo(5), alloter2.allot(hashRowInfo6));
        Assert.assertEquals(newSegInfo(6), alloter2.allot(hashRowInfo7));
        Assert.assertEquals(newSegInfo(7), alloter2.allot(hashRowInfo8));
        Assert.assertEquals(newSegInfo(8), alloter2.allot(hashRowInfo9));
        Mockito.verify(bucketService, Mockito.times(9)).save(Mockito.any(SwiftSegmentBucketElement.class));
    }
}
