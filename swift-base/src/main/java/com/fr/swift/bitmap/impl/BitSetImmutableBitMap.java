package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.BitSet;

/**
 * @author anchore
 */
public class BitSetImmutableBitMap extends BaseBitSetBitMap {
    private BitSetImmutableBitMap(BitSet bitset) {
        super(bitset);
    }

    static ImmutableBitMap newInstance(BitSet bitset) {
        return new BitSetImmutableBitMap(bitset);
    }

    public static ImmutableBitMap newInstance() {
        return new BitSetImmutableBitMap(new BitSet());
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return this;
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.and(extract(index));

        return newInstance(copy);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return index;
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.or(extract(index));

        return newInstance(copy);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return BitMaps.EMPTY_IMMUTABLE;
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.andNot(extract(index));

        return newInstance(copy);
    }

    @Override
    public ImmutableBitMap getNot(int rowCount) {
        BitSet copy = (BitSet) bitset.clone();
        copy.flip(0, rowCount);

        return newInstance(copy);
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(bitset);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.BIT_SET_IMMUTABLE;
    }
}
