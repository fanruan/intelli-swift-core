package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 * @author anchore
 * @date 2019/3/18
 */
public class BitmapMemMeter {

    public static long meter(ImmutableBitMap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        switch (bitmap.getType()) {
            case ROARING_IMMUTABLE:
            case ROARING_MUTABLE:
                MutableRoaringBitmap mutableRoaringBitmap = ((BaseRoaringBitMap) bitmap).bitmap;
                return mutableRoaringBitmap.getLongSizeInBytes();
            case RANGE:
            case ALL_SHOW:
            case ID:
                return 8;
            case BIT_SET_IMMUTABLE:
            case BIT_SET_MUTABLE:
            default:
                throw new UnsupportedOperationException();
        }
    }
}