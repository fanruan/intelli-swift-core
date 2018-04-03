package com.fr.swift.bitmap.impl;

import com.fineio.base.Bits;
import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.ImmutableRoaringBitmap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;

/**
 * @author anchore
 */
public final class IdBitMap extends AbstractBitMap {
    private final int id;

    private IdBitMap(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0: " + id);
        }
        this.id = id;
    }

    public static ImmutableBitMap newInstance(int id) {
        return new IdBitMap(id);
    }

    @Override
    public boolean contains(int index) {
        return id == index;
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (!index.contains(id)) {
            return BitMaps.EMPTY_IMMUTABLE;
        }
        return this;
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return this;
        }

        if (index instanceof AllShowBitMap) {
            return index;
        }

        if (index instanceof IdBitMap) {
            IdBitMap idMap = (IdBitMap) index;
            if (id == idMap.id) {
                return clone();
            }

            MutableBitMap rm = RoaringMutableBitMap.newInstance();
            rm.add(id);
            rm.add(idMap.id);
            return rm.clone();
        }

        MutableBitMap rm = RoaringMutableBitMap.newInstance();
        rm.add(id);
        return index.getOr(rm);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index.contains(id)) {
            return BitMaps.EMPTY_IMMUTABLE;
        }
        return this;
    }

    @Override
    public ImmutableBitMap getNot(int rowCount) {
        MutableBitMap rm = RoaringMutableBitMap.newInstance();
        rm.add(id);
        return rm.getNot(rowCount);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void traversal(TraversalAction action) {
        action.actionPerformed(id);
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        return action.actionPerformed(id);
    }

    @Override
    public int getCardinality() {
        return 1;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(id);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ID;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[4];
        Bits.putInt(bytes, 0, id);
        return bytes;
    }

    static ImmutableBitMap fromBytes(byte[] bytes) {
        return newInstance(Bits.getInt(bytes, 0));
    }

    @Override
    public String toString() {
        return "{" + id + '}';
    }

    @Override
    protected ImmutableRoaringBitmap getBitMap() {
        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.add(id);
        return bitmap;
    }
}