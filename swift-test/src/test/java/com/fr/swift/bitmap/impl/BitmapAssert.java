package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import org.junit.Assert;

/**
 * @author anchore
 * @date 2018/7/9
 */
public class BitmapAssert {
    public static void assertEquals(ImmutableBitMap b1, ImmutableBitMap b2) {
        Assert.assertEquals(b1.getCardinality(), b2.getCardinality());
        b1.traversal(i -> {
            if (!b2.contains(i)) {
                Assert.fail();
            }
        });
        b2.traversal(i -> {
            if (!b1.contains(i)) {
                Assert.fail();
            }
        });
    }
}