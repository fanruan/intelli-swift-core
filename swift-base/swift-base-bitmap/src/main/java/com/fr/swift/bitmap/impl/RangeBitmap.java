package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Assert;
import com.fr.swift.util.IoUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class RangeBitmap extends AbstractBitMap {
    /**
     * [start, end)
     */
    final int start, end;

    public RangeBitmap(int start, int end) {
        Assert.isTrue(start <= end, "start > end, illegal");
        this.start = start;
        this.end = end;
    }

    public static ImmutableBitMap of(int start, int end) {
        return new RangeBitmap(start, end);
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        switch (index.getType()) {
            case RANGE:
                return FasterAggregation.and(this, ((RangeBitmap) index));
            default:
                return index.getAnd(this);
        }
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        switch (index.getType()) {
            case RANGE:
                return FasterAggregation.or(this, ((RangeBitmap) index));
            default:
                return index.getOr(this);
        }
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        switch (index.getType()) {
            case RANGE:
                return FasterAggregation.andNot(this, ((RangeBitmap) index));
            default:
                MutableBitMap bitmap = toRealBitmap();
                bitmap.andNot(index);
                return bitmap;
        }
    }

    @Override
    public ImmutableBitMap getNot(int bound) {
        if (bound <= end) {
            return of(0, start);
        }
        MutableRoaringBitmap b = new MutableRoaringBitmap();
        b.flip(0L, start);
        b.flip((long) end, bound);
        return RoaringImmutableBitMap.of(b);
    }

    @Override
    public boolean contains(int index) {
        return index >= start && index < end;
    }

    @Override
    public ImmutableBitMap clone() {
        return of(start, end);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.RANGE;
    }

    private MutableBitMap toRealBitmap() {
        MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
        bitmap.flip((long) start, (long) end);
        return RoaringMutableBitMap.of(bitmap);
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(9)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(getType().getHead())
                .putInt(start).putInt(end).array();
    }

    @Override
    public void writeBytes(OutputStream output) {
        try {
            output.write(toBytes());
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            IoUtil.close(output);
        }
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
    public String toString() {
        return String.format("{%d, ..., %d}", start, end - 1);
    }

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {

            private int cursor = start;

            @Override
            public int nextInt() {
                return cursor++;
            }

            @Override
            public boolean hasNext() {
                return cursor < end;
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
}