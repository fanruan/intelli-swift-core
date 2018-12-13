package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.structure.IntIterable.IntIterator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/7/13
 */
public class BitmapTest {
    @Test
    public void and() {
        ImmutableBitMap emptyBitmap = BitMaps.EMPTY_IMMUTABLE,
                rangeBitmap = new RangeBitmap(6, 10),
                allShowBitmap = AllShowBitMap.of(100);
        MutableBitMap roaringBitmap = RoaringMutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 7, 9, 50));

        // 自反
        BitmapAssert.equals(roaringBitmap.getAnd(rangeBitmap), rangeBitmap.getAnd(roaringBitmap));

        BitmapAssert.equals(emptyBitmap.getAnd(rangeBitmap), emptyBitmap);

        BitmapAssert.equals(rangeBitmap.getAnd(allShowBitmap), rangeBitmap);

        BitmapAssert.equals(roaringBitmap.getAnd(rangeBitmap), RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(7, 9)));

        BitmapAssert.equals(roaringBitmap.getAnd(allShowBitmap), roaringBitmap);

        roaringBitmap.and(rangeBitmap);
        BitmapAssert.equals(roaringBitmap, RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(7, 9)));

        roaringBitmap.and(allShowBitmap);
        BitmapAssert.equals(roaringBitmap, roaringBitmap);

        roaringBitmap.and(emptyBitmap);
        BitmapAssert.equals(roaringBitmap, RoaringImmutableBitMap.of());
    }

    @Test
    public void or() {
        ImmutableBitMap emptyBitmap = BitMaps.EMPTY_IMMUTABLE,
                rangeBitmap = new RangeBitmap(6, 10),
                allShowBitmap = AllShowBitMap.of(100);
        MutableBitMap roaringBitmap = RoaringMutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 7, 9, 50));

        // 自反
        BitmapAssert.equals(roaringBitmap.getOr(rangeBitmap), rangeBitmap.getOr(roaringBitmap));

        BitmapAssert.equals(emptyBitmap.getOr(rangeBitmap), rangeBitmap);

        BitmapAssert.equals(rangeBitmap.getOr(allShowBitmap), allShowBitmap);

        BitmapAssert.equals(roaringBitmap.getOr(rangeBitmap), RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 6, 7, 8, 9, 50)));

        BitmapAssert.equals(roaringBitmap.getOr(allShowBitmap), allShowBitmap);

        roaringBitmap.or(emptyBitmap);
        BitmapAssert.equals(roaringBitmap, roaringBitmap);

        roaringBitmap.or(rangeBitmap);
        BitmapAssert.equals(roaringBitmap, RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 6, 7, 8, 9, 50)));

        roaringBitmap.or(allShowBitmap);
        BitmapAssert.equals(roaringBitmap, allShowBitmap);
    }

    @Test
    public void andNot() {
        ImmutableBitMap emptyBitmap = BitMaps.EMPTY_IMMUTABLE,
                rangeBitmap = new RangeBitmap(6, 10),
                allShowBitmap = AllShowBitMap.of(100);
        MutableBitMap roaringBitmap = RoaringMutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 7, 9, 50));

        BitmapAssert.equals(emptyBitmap.getAndNot(rangeBitmap), emptyBitmap);

        BitmapAssert.equals(rangeBitmap.getAndNot(allShowBitmap), emptyBitmap);

        BitmapAssert.equals(roaringBitmap.getAndNot(rangeBitmap), RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 50)));

        BitmapAssert.equals(roaringBitmap.getAndNot(allShowBitmap), emptyBitmap);

        roaringBitmap.andNot(emptyBitmap);
        BitmapAssert.equals(roaringBitmap, roaringBitmap);

        roaringBitmap.andNot(rangeBitmap);
        BitmapAssert.equals(roaringBitmap, RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 50)));

        roaringBitmap.andNot(allShowBitmap);
        BitmapAssert.equals(roaringBitmap, emptyBitmap);
    }

    @Test
    public void not() {
        ImmutableBitMap emptyBitmap = BitMaps.EMPTY_IMMUTABLE,
                rangeBitmap = new RangeBitmap(6, 10),
                allShowBitmap = AllShowBitMap.of(100);
        MutableBitMap roaringBitmap = RoaringMutableBitMap.of(MutableRoaringBitmap.bitmapOf(3, 4, 5, 7, 9, 50));

        BitmapAssert.equals(emptyBitmap.getNot(100), allShowBitmap);

        BitmapAssert.equals(rangeBitmap.getNot(100), RoaringMutableBitMap.of(MutableRoaringBitmap.or(
                MutableRoaringBitmap.add(new MutableRoaringBitmap(), 0, 6),
                MutableRoaringBitmap.add(new MutableRoaringBitmap(), 10, 100))));

        BitmapAssert.equals(roaringBitmap.getNot(11), RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(0, 1, 2, 6, 8, 10, 50)));

        roaringBitmap.not(11);
        BitmapAssert.equals(roaringBitmap, RoaringImmutableBitMap.of(MutableRoaringBitmap.bitmapOf(0, 1, 2, 6, 8, 10, 50)));
    }

    @Test
    public void testIterateRangeBitmap() {
        ImmutableBitMap bitmap = new RangeBitmap(1, 4);
        IntIterator itr = bitmap.intIterator();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(1, itr.nextInt());
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(2, itr.nextInt());
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(3, itr.nextInt());
    }
}