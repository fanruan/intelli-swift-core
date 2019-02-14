package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;

import java.util.BitSet;
import java.util.Iterator;

/**
 * @author anchore
 */
public abstract class BaseBitSetBitMap extends AbstractBitMap {
    final BitSet bitset;

    BaseBitSetBitMap(BitSet bitset) {
        this.bitset = bitset;
    }

    static BitSet extract(ImmutableBitMap immutableMap) {
        if (immutableMap instanceof BaseBitSetBitMap) {
            return ((BaseBitSetBitMap) immutableMap).bitset;
        }
        final BitSet bitSet = new BitSet();
        immutableMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                bitSet.set(row);
            }
        });
        return bitSet;
    }

    @Override
    public boolean contains(int index) {
        return bitset.get(index);
    }

    @Override
    public void traversal(TraversalAction action) {
        for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
            action.actionPerformed(i);
        }
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
            if (action.actionPerformed(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {

            int i = bitset.nextSetBit(0);

            @Override
            public int nextInt() {
                int prevI = i;
                i = bitset.nextSetBit(i + 1);
                return prevI;
            }

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public Integer next() {
                return nextInt();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator<Integer> iterator() {
        return intIterator();
    }

    @Override
    public int getCardinality() {
        return bitset.cardinality();
    }

    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return bitset.toString();
    }
}
