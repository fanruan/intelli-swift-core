package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

/**
 * @author anchore
 */
public final class AllShowBitMap extends RangeBitmap {
    private AllShowBitMap(int rowCount) {
        super(0, rowCount);
    }

    public static ImmutableBitMap newInstance(int rowCount) {
        return new AllShowBitMap(rowCount);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(end - 1);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ALL_SHOW;
    }
}