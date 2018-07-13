package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 * @author anchore
 * @date 2018/7/6
 * <p>
 * 比FastAggregation还要快
 */
public final class FasterAggregation {
    public static ImmutableBitMap or(Iterable<ImmutableBitMap> bitmaps) {
        MutableRoaringBitmap r = new MutableRoaringBitmap();
        for (ImmutableBitMap bitmap : bitmaps) {
            r.naivelazyor(((BaseRoaringBitMap) bitmap).bitmap);
        }
        r.repairAfterLazy();
        return RoaringImmutableBitMap.of(r);
    }

    public static ImmutableBitMap or(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            MutableRoaringBitmap b = new MutableRoaringBitmap();
            b.flip((long) b1.start, b1.end);
            b.flip((long) b2.start, b2.end);
            return RoaringImmutableBitMap.of(b);
        }

        int start = b1.start < b2.start ? b1.start : b2.start;
        int end = b1.end > b2.end ? b1.end : b2.end;
        return RangeBitmap.of(start, end);
    }

    public static ImmutableBitMap and(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            return new EmptyBitmap();
        }

        int start = b1.start > b2.start ? b1.start : b2.start;
        int end = b1.end < b2.end ? b1.end : b2.end;
        return RangeBitmap.of(start, end);
    }

    public static ImmutableBitMap andNot(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            return b1;
        }

        int start = b1.start > b2.start ? b1.start : b2.start;
        int end = b1.end < b2.end ? b1.end : b2.end;

        if (start == b1.start) {
            return RangeBitmap.of(end, b1.end);
        }
        if (end == b1.end) {
            return RangeBitmap.of(b1.start, start);
        }

        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.add((long) b1.start, start);
        bitmap.add((long) end, b1.end);
        return RoaringImmutableBitMap.of(bitmap);
    }

    static ImmutableBitMap andNot(RoaringImmutableBitMap b1, RangeBitmap b2) {
        if (b1.isEmpty()) {
            return b1;
        }

        int b1Start = b1.bitmap.select(0), b1End = b1.bitmap.select(b1.bitmap.getCardinality() - 1);

        if (b1End < b2.start || b2.end < b1Start) {
            return b1;
        }

        int start = b1Start > b2.start ? b1Start : b2.start;
        int end = b1End < b2.end ? b1End : b2.end;

        MutableRoaringBitmap bitmap = b1.bitmap.clone();
        bitmap.remove((long) start, end);
        return RoaringImmutableBitMap.of(bitmap);
    }

    static void andNot(RoaringMutableBitMap b1, RangeBitmap b2) {
        if (b1.isEmpty()) {
            return;
        }

        int b1Start = b1.bitmap.select(0), b1End = b1.bitmap.select(b1.bitmap.getCardinality() - 1);

        if (b1End < b2.start || b2.end < b1Start) {
            return;
        }

        int start = b1Start > b2.start ? b1Start : b2.start;
        int end = b1End < b2.end ? b1End : b2.end;

        b1.bitmap.remove((long) start, end);
    }
}