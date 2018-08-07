package com.fr.swift.bitmap.impl;

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
        return new byte[0];
    }

    @Override
    public String toString() {
        return "{}";
    }
}