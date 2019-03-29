package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;

/**
 * @author anchore
 * @date 2018/7/6
 */
public class EmptyBitmap extends RangeBitmap {
    public EmptyBitmap() {
        super(0, 0);
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