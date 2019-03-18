package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author anchore
 */
public final class AllShowBitMap extends RangeBitmap {
    private AllShowBitMap(int rowCount) {
        super(0, rowCount);
    }

    public static ImmutableBitMap of(int rowCount) {
        return new AllShowBitMap(rowCount);
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(4)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(end).array();
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        return index;
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        return this;
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        return index.getNot(end);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(end);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ALL_SHOW;
    }
}