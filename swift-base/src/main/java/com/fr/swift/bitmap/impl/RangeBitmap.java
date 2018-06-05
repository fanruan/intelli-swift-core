package com.fr.swift.bitmap.impl;

import com.fineio.base.Bits;
import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.ImmutableRoaringBitmap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class RangeBitmap extends AbstractBitMap {
    final int start, end;

    public RangeBitmap(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        this.start = start;
        this.end = end;
    }

    @Override
    protected ImmutableRoaringBitmap getBitMap() {
        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.flip((long) start, (long) end);
        return bitmap;
    }

    private MutableBitMap toRealBitmap() {
        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.flip((long) start, (long) end);
        return RoaringMutableBitMap.newInstance(bitmap);
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        MutableBitMap bitmap = toRealBitmap();
        bitmap.and(index);
        return bitmap;
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        MutableBitMap bitmap = toRealBitmap();
        bitmap.or(index);
        return bitmap;
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        MutableBitMap bitmap = toRealBitmap();
        bitmap.andNot(index);
        return bitmap;
    }

    @Override
    public ImmutableBitMap getNot(int rowCount) {
        MutableBitMap bitmap = toRealBitmap();
        bitmap.not(rowCount);
        return bitmap;
    }

    @Override
    public boolean contains(int index) {
        return index >= start && index < end;
    }

    @Override
    public ImmutableBitMap clone() {
        return new RangeBitmap(start, end);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.RANGE;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[8];
        Bits.putInt(bytes, 0, start);
        Bits.putInt(bytes, 4, end);
        return bytes;
    }

    public static ImmutableBitMap fromBytes(byte[] bytes, int offset) {
        return new RangeBitmap(Bits.getInt(bytes, offset), Bits.getInt(bytes, offset + 4));
    }

    @Override
    public void traversal(TraversalAction action) {
        for (int i = start; i < end; i++) {
            action.actionPerformed(i);
        }
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        for (int i = start; i < end; i++) {
            if (action.actionPerformed(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCardinality() {
        return end - start;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("{%d, ..., %d}", start, end);
    }
}