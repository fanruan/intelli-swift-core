package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

/**
 * @author anchore
 * @date 2018/7/6
 */
public class EmptyBitmap extends RangeBitmap {
    public EmptyBitmap() {
        super(0, 0);
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        return this;
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        return index;
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        return this;
    }

    @Override
    public ImmutableBitMap getNot(int bound) {
        return new RangeBitmap(0, bound);
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{getType().getHead()};
    }

    @Override
    public BitMapType getType() {
        return BitMapType.EMPTY;
    }

    @Override
    public String toString() {
        return "{}";
    }
}