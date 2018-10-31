package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import org.junit.Assert;

/**
 * @author anchore
 * @date 2018/7/9
 */
public class BitmapAssert {
    public static void equals(final ImmutableBitMap b1, final ImmutableBitMap b2) {
        Assert.assertEquals(b1.getCardinality(), b2.getCardinality());
        b1.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int i) {
                if (!b2.contains(i)) {
                    Assert.fail();
                }
            }
        });
        b2.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int i) {
                if (!b1.contains(i)) {
                    Assert.fail();
                }
            }
        });
    }
}