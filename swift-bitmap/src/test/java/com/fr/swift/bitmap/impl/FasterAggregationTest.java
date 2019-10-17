package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/7/9
 */
public class FasterAggregationTest {

    @Test
    public void or() {
        MutableBitMap bitmap = RoaringMutableBitMap.of();
        bitmap.add(0);
        bitmap.add(4);
        BitmapAssert.equals(
                bitmap,
                FasterAggregation.or(new RangeBitmap(0, 1), new RangeBitmap(4, 5)));

        BitmapAssert.equals(
                RangeBitmap.of(0, 10),
                FasterAggregation.or(new RangeBitmap(0, 5), new RangeBitmap(2, 10)));
        BitmapAssert.equals(
                RangeBitmap.of(0, 10),
                FasterAggregation.or(new RangeBitmap(0, 2), new RangeBitmap(2, 10)));

        BitmapAssert.equals(
                RangeBitmap.of(0, 100),
                FasterAggregation.or(new RangeBitmap(0, 100), new RangeBitmap(4, 6)));
        BitmapAssert.equals(
                RangeBitmap.of(0, 100),
                FasterAggregation.or(new RangeBitmap(0, 100), new RangeBitmap(0, 100)));

        BitmapAssert.equals(
                RangeBitmap.of(-100, 6),
                FasterAggregation.or(new RangeBitmap(0, 2), new RangeBitmap(-100, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(0, 20),
                FasterAggregation.or(new RangeBitmap(5, 20), new RangeBitmap(0, 9)));

        bitmap = RoaringMutableBitMap.of();
        bitmap.add(100);
        bitmap.add(0);
        BitmapAssert.equals(
                bitmap,
                FasterAggregation.or(new RangeBitmap(100, 101), new RangeBitmap(0, 1)));
    }

    @Test
    public void and() {
        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(0, 2), new RangeBitmap(4, 6)));
        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(0, 4), new RangeBitmap(4, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(2, 5),
                FasterAggregation.and(new RangeBitmap(0, 5), new RangeBitmap(2, 10)));

        BitmapAssert.equals(
                RangeBitmap.of(4, 6),
                FasterAggregation.and(new RangeBitmap(0, 100), new RangeBitmap(4, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(4, 6),
                FasterAggregation.and(new RangeBitmap(4, 6), new RangeBitmap(4, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(2, 4),
                FasterAggregation.and(new RangeBitmap(2, 4), new RangeBitmap(0, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(5, 9),
                FasterAggregation.and(new RangeBitmap(0, 9), new RangeBitmap(5, 20)));
        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(5, 20), new RangeBitmap(0, 5)));

        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(100, 101), new RangeBitmap(0, 1)));
    }


    private int[][] ranges = {
            {10, 20, 1, 5},
            {10, 20, 1, 10},
            {10, 20, 10, 11},
            {10, 20, 5, 11},
            {10, 20, 11, 14},
            {10, 20, 11, 20},
            {10, 20, 10, 20},
            {10, 20, 20, 30},
            {10, 20, 11, 25},
            {10, 20, 25, 30},
    };

    @Test
    public void rangeAndNotRange() {
        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]);
            bitmap.andNot(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.andNot(new RangeBitmap(range[0], range[1]), new RangeBitmap(range[2], range[3])));
        }

        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]);
            bitmap.andNot(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.andNot(new RangeBitmap(range[2], range[3]), new RangeBitmap(range[0], range[1])));
        }
    }

    @Test
    public void rangeAndRange() {
        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]);
            bitmap.and(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.and(new RangeBitmap(range[0], range[1]), new RangeBitmap(range[2], range[3])));
        }

        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]);
            bitmap.and(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.and(new RangeBitmap(range[2], range[3]), new RangeBitmap(range[0], range[1])));
        }
    }

    @Test
    public void rangeOrRange() {
        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]);
            bitmap.or(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.or(new RangeBitmap(range[0], range[1]), new RangeBitmap(range[2], range[3])));
        }

        for (int[] range : ranges) {
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[2], range[3]);
            bitmap.or(MutableRoaringBitmap.add(new MutableRoaringBitmap(), (long) range[0], range[1]));

            BitmapAssert.equals(
                    RoaringImmutableBitMap.of(bitmap),
                    FasterAggregation.or(new RangeBitmap(range[2], range[3]), new RangeBitmap(range[0], range[1])));
        }
    }
}