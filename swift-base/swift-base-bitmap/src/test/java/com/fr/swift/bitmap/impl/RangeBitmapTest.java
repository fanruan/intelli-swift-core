package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author anchore
 * @date 2019/3/12
 */
public class RangeBitmapTest {

    @Test
    public void of() {
        ImmutableBitMap bitmap = RangeBitmap.of(100, 200);
        Assert.assertEquals(100, bitmap.getCardinality());
        for (int i = 100; i < 200; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }
}