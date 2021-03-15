package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.array.IntListFactory;

import java.util.List;

/**
 * @author anchore
 * @date 2018/7/6
 * <p>
 * 比FastAggregation还要快
 */
public final class FasterAggregation {
    public static ImmutableBitMap or(List<ImmutableBitMap> bitmaps) {
        if (bitmaps == null || bitmaps.isEmpty()) {
            return BitMaps.EMPTY_IMMUTABLE;
        }
        if (bitmaps.size() == 1) {
            return bitmaps.get(0);
        }
        MutableRoaringBitmap r = new MutableRoaringBitmap();
        final IntList list = IntListFactory.createIntList();
        for (ImmutableBitMap bitmap : bitmaps) {
            if (bitmap.isEmpty()) {
                continue;
            }
            if (bitmap.getType() == BitMapType.ID) {
                list.add(((IdBitMap) bitmap).getId());
            } else if (bitmap instanceof BaseRoaringBitMap) {
                r.naivelazyor(((BaseRoaringBitMap) bitmap).bitmap);
            }
        }
        r.repairAfterLazy();
        ImmutableBitMap bitMap = RoaringImmutableBitMap.of(r);
        if (list.size() > 0) {
            bitMap = bitMap.getOr(BitMaps.newImmutableBitMap(list));
        }
        return bitMap;
    }

    public static ImmutableBitMap compose(List<ImmutableBitMap> bitmaps, final int[] offsets) {
        if (bitmaps == null || bitmaps.size() == 0) {
            return BitMaps.EMPTY_IMMUTABLE;
        }
        boolean allFull = true;
        for (ImmutableBitMap bitmap : bitmaps) {
            if (!bitmap.isFull()) {
                allFull = false;
                break;
            }
        }
        if (allFull) {
            return AllShowBitMap.of(offsets[offsets.length - 1]);
        }
        final MutableRoaringBitmap rb = new MutableRoaringBitmap();
        for (int i = 0; i < bitmaps.size(); i++) {
            if (i == 0 && bitmaps.get(i) instanceof BaseRoaringBitMap) {
                // 第一个roaring可以直接or，后面的只能add了
                rb.or(((BaseRoaringBitMap) bitmaps.get(i)).bitmap);
            } else if (bitmaps.get(i) instanceof RangeBitmap) {
                RangeBitmap rangeBitmap = (RangeBitmap) bitmaps.get(i);
                rb.add((long) rangeBitmap.start + offsets[i], rangeBitmap.end + offsets[i]);
            } else {
                final int finalI = i;
                bitmaps.get(i).traversal(new TraversalAction() {
                    @Override
                    public void actionPerformed(int row) {
                        rb.add(row + offsets[finalI]);
                    }
                });
            }
        }

        return RoaringImmutableBitMap.of(rb);
    }

    public static ImmutableBitMap or(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            MutableRoaringBitmap b = new MutableRoaringBitmap();
            b.flip((long) b1.start, b1.end);
            b.flip((long) b2.start, b2.end);
            return RoaringImmutableBitMap.of(b);
        }

        int start = Math.min(b1.start, b2.start);
        int end = Math.max(b1.end, b2.end);
        return RangeBitmap.of(start, end);
    }

    public static ImmutableBitMap and(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            return new EmptyBitmap();
        }

        int start = Math.max(b1.start, b2.start);
        int end = Math.min(b1.end, b2.end);
        return RangeBitmap.of(start, end);
    }

    public static ImmutableBitMap andNot(RangeBitmap b1, RangeBitmap b2) {
        if (b1.end < b2.start || b2.end < b1.start) {
            return b1;
        }

        int start = Math.max(b1.start, b2.start);
        int end = Math.min(b1.end, b2.end);

        if (start == b1.start) {
            return RangeBitmap.of(end, b1.end);
        }
        if (end == b1.end) {
            return RangeBitmap.of(b1.start, start);
        }

        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.add((long) b1.start, start);
        bitmap.add((long) end, b1.end);
        return RoaringImmutableBitMap.of(bitmap);
    }

    static ImmutableBitMap andNot(RoaringImmutableBitMap b1, RangeBitmap b2) {
        if (b1.isEmpty()) {
            return b1;
        }

        int b1Start = b1.bitmap.select(0), b1End = b1.bitmap.select(b1.bitmap.getCardinality() - 1);

        if (b1End < b2.start || b2.end < b1Start) {
            return b1;
        }

        int start = Math.max(b1Start, b2.start);
        int end = Math.min(b1End, b2.end);

        MutableRoaringBitmap bitmap = b1.bitmap.clone();
        bitmap.remove((long) start, end);
        return RoaringImmutableBitMap.of(bitmap);
    }

    static void andNot(RoaringMutableBitMap b1, RangeBitmap b2) {
        if (b1.isEmpty()) {
            return;
        }

        int b1Start = b1.bitmap.select(0), b1End = b1.bitmap.select(b1.bitmap.getCardinality() - 1);

        if (b1End < b2.start || b2.end < b1Start) {
            return;
        }

        int start = Math.max(b1Start, b2.start);
        int end = Math.min(b1End, b2.end);

        b1.bitmap.remove((long) start, end);
    }
}