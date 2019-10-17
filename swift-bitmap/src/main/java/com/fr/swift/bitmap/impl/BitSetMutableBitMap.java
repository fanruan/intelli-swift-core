package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;

import java.util.BitSet;

/**
 * @author anchore
 */
public class BitSetMutableBitMap extends BaseBitSetBitMap implements MutableBitMap {
    private BitSetMutableBitMap(BitSet bitset) {
        super(bitset);
    }

    static MutableBitMap newInstance(BitSet bitset) {
        return new BitSetMutableBitMap(bitset);
    }

    public static MutableBitMap newInstance() {
        return newInstance(new BitSet());
    }

    @Override
    public void or(ImmutableBitMap index) {
        bitset.or(extract(index));
    }

    @Override
    public void and(ImmutableBitMap index) {
        bitset.and(extract(index));
    }

    @Override
    public void not(int bound) {
        bitset.flip(0, bound);
    }

    @Override
    public void andNot(ImmutableBitMap index) {
        bitset.andNot(extract(index));
    }

    @Override
    public void add(int index) {
        bitset.set(index);
    }

    @Override
    public void remove(int index) {
        bitset.clear(index);
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return clone();
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.and(extract(index));

        return BitSetImmutableBitMap.of(copy);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return index;
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.or(extract(index));

        return BitSetImmutableBitMap.of(copy);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return BitMaps.EMPTY_IMMUTABLE;
        }

        BitSet copy = (BitSet) bitset.clone();
        copy.andNot(extract(index));

        return BitSetImmutableBitMap.of(copy);
    }

    @Override
    public ImmutableBitMap getNot(int bound) {
        BitSet copy = (BitSet) bitset.clone();
        copy.flip(0, bound);

        return BitSetImmutableBitMap.of(copy);
    }

    @Override
    public ImmutableBitMap clone() {
        return BitSetImmutableBitMap.of((BitSet) bitset.clone());
    }

    @Override
    public BitMapType getType() {
        return BitMapType.BIT_SET_MUTABLE;
    }
}