package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;
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
                RangeBitmap.of(0, 2),
                FasterAggregation.and(new RangeBitmap(0, 2), new RangeBitmap(-100, 6)));

        BitmapAssert.equals(
                RangeBitmap.of(5, 9),
                FasterAggregation.and(new RangeBitmap(0, 9), new RangeBitmap(5, 20)));
        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(5, 20), new RangeBitmap(0, 5)));

        BitmapAssert.equals(
                new EmptyBitmap(),
                FasterAggregation.and(new RangeBitmap(100, 101), new RangeBitmap(-100, 0)));
    }
}