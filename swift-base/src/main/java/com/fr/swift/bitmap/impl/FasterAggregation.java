package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 * @author anchore
 * @date 2018/7/6
 * <p>
 * 比FastAggregation还要快
 */
public final class FasterAggregation {
    public static ImmutableBitMap and(ImmutableBitMap b1, ImmutableBitMap b2) {
        BitMapType b1Type = b1.getType();
        BitMapType b2Type = b2.getType();
        return null;
    }

    public static ImmutableBitMap or(Iterable<ImmutableBitMap> bitmaps) {
        MutableRoaringBitmap r = new MutableRoaringBitmap();
        for (ImmutableBitMap bitmap : bitmaps) {
            r.naivelazyor(((BaseRoaringBitMap) bitmap).bitmap);
        }
        r.repairAfterLazy();
        return RoaringImmutableBitMap.of(r);
    }

    public static ImmutableBitMap or(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end >= b2.start) {
            return RangeBitmap.of(b1.start, b2.end);
        }
        if (b2.end >= b1.start) {
            return RangeBitmap.of(b2.start, b1.end);
        }
        MutableRoaringBitmap b = new MutableRoaringBitmap();
        b.flip((long) b1.start, b1.end);
        b.flip((long) b2.start, b2.end);
        return RoaringImmutableBitMap.of(b);
    }

    public static ImmutableBitMap and(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end >= b2.start) {
            return RangeBitmap.of(b2.start, b1.end);
        }
        if (b2.end >= b1.start) {
            return RangeBitmap.of(b1.start, b2.end);
        }
        return new EmptyBitmap();
    }
}