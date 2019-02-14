package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;

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
    public abstract ImmutableBitMap clone();
}
