package com.fr.swift.bitmap.impl;

import com.fineio.base.Bits;
import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.ImmutableRoaringBitmap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;

/**
 * @author anchore
 */
public final class AllShowBitMap extends AbstractBitMap {
    private final int rowCount;

    private AllShowBitMap(int rowCount) {
        if (rowCount < 0) {
            throw new IllegalArgumentException("rowCount < 0: " + rowCount);
        }
        this.rowCount = rowCount;
    }

    public static ImmutableBitMap newInstance(int rowCount) {
        return new AllShowBitMap(rowCount);
    }

    @Override
    public boolean contains(int index) {
        return index < rowCount && index >= 0;
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        return index.clone();
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        return this;
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return this;
        }
        return index.getNot(rowCount);
    }

    @Override
    public ImmutableBitMap getNot(int rowCount) {
        return BitMaps.EMPTY_IMMUTABLE;
    }

    @Override
    public boolean isEmpty() {
        return rowCount == 0;
    }

    @Override
    public void traversal(TraversalAction action) {
        for (int i = 0; i < rowCount; i++) {
            action.actionPerformed(i);
        }
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        for (int i = 0; i < rowCount; i++) {
            if (action.actionPerformed(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCardinality() {
        return rowCount;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(rowCount);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ALL_SHOW;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[4];
        Bits.putInt(bytes, 0, rowCount);
        return bytes;
    }

    public static ImmutableBitMap fromBytes(byte[] bytes) {
        return fromBytes(bytes, 0);
    }

    public static ImmutableBitMap fromBytes(byte[] bytes, int off) {
        return newInstance(Bits.getInt(bytes, off));
    }

    @Override
    public String toString() {
        return "{0, ..., " + rowCount + '}';
    }

    @Override
    protected ImmutableRoaringBitmap getBitMap() {
        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.flip(0L, rowCount);
        return bitmap;
    }
}
