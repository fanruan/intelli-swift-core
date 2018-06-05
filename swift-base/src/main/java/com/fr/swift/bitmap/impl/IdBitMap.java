package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

/**
 * @author anchore
 */
public final class IdBitMap extends RangeBitmap {
    private IdBitMap(int id) {
        super(id, id + 1);
    }

    public static ImmutableBitMap newInstance(int id) {
        return new IdBitMap(id);
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(start);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ID;
    }

    @Override
    public String toString() {
        return String.format("{%d}", start);
    }
}