package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author anchore
 */
public final class IdBitMap extends RangeBitmap {
    private IdBitMap(int id) {
        super(id, id + 1);
    }

    public int getId() {
        return start;
    }

    public static ImmutableBitMap of(int id) {
        return new IdBitMap(id);
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index.contains(start)) {
            return this;
        }
        return new EmptyBitmap();
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index.contains(start)) {
            return index;
        }
        return super.getOr(index);
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
        return ByteBuffer.allocate(5)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(getType().getHead())
                .putInt(start).array();
    }

    @Override
    public String toString() {
        return String.format("{%d}", start);
    }
}