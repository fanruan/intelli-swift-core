package com.fr.swift.bitmap.impl;

import com.fineio.base.Bits;
import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

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

    public static ImmutableBitMap ofBytes(byte[] bytes, int offset) {
        return of(Bits.getInt(bytes, offset));
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[4];
        Bits.putInt(bytes, 0, end);
        return bytes;
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