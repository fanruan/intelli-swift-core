package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;

/**
 * @author Lyon
 * @date 2017/11/29
 */
public abstract class AbstractBitMap implements ImmutableBitMap {
    @Override
    public ImmutableBitMap toBitMap() {
        return this;
    }

    @Override
    public boolean isFull() {
        return getType() == BitMapType.ALL_SHOW;
    }

    @Override
    public boolean isEmpty() {
        return getCardinality() == 0;
    }

    @Override
    public IntIterator iterator() {
        return intIterator();
    }

    @Override
    public abstract ImmutableBitMap clone();
}
