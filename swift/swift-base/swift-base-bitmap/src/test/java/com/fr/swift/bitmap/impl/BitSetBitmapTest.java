package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.structure.IntIterable.IntIterator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.BitSet;

/**
 * @author anchore
 */
public class BitSetBitmapTest extends RoaringBitmapTest {
    @Override
    MutableBitMap getMutableBitMap() {
        return BitSetMutableBitMap.newInstance();
    }

    @Override
    ImmutableBitMap getImmutableBitMap() {
        return BitSetImmutableBitMap.of();
    }

    @Ignore
    @Override
    public void testToBytes() {
    }

    @Test
    public void testIterate() {
        BitSet bitset = new BitSet();
        bitset.set(2);
        bitset.set(4);
        bitset.set(6);
        ImmutableBitMap bitmap = BitSetImmutableBitMap.of(bitset);
        IntIterator itr = bitmap.intIterator();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(2, itr.nextInt());
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(4, itr.nextInt());
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(6, itr.nextInt());
    }
}
