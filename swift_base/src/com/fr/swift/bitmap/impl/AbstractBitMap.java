package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.ImmutableRoaringBitmap;

/**
 * Created by Lyon on 2017/11/29.
 */
public abstract class AbstractBitMap implements ImmutableBitMap {

    protected abstract ImmutableRoaringBitmap getBitMap();

    @Override
    public abstract ImmutableBitMap clone();

    @Override
    public ImmutableBitMap toBitMap() {
        return this;
    }
}
