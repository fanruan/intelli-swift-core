package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

import java.nio.ByteBuffer;

/**
 * @author anchore
 */
public final class IdBitMap extends RangeBitmap {
    private IdBitMap(int id) {
        super(id, id + 1);
    }

    public static ImmutableBitMap of(int id) {
        return new IdBitMap(id);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(start);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ID;
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(4).putInt(start).array();
    }

    @Override
    public String toString() {
        return String.format("{%d}", start);
    }
}