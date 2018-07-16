package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.log.SwiftLoggers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author anchore
 */
public class RoaringMutableBitMap extends RoaringImmutableBitMap implements MutableBitMap {
    private RoaringMutableBitMap(MutableRoaringBitmap bitmap) {
        super(bitmap);
    }

    static MutableBitMap of(MutableRoaringBitmap bitmap) {
        return new RoaringMutableBitMap(bitmap);
    }

    public static MutableBitMap of() {
        return of(new MutableRoaringBitmap());
    }

    private static MutableBitMap fromByteBuffer(final ByteBuffer bb) {
        MutableRoaringBitmap another = new MutableRoaringBitmap();
        DataInputStream dis = new DataInputStream(new InputStream() {
            @Override
            public int read() {
                return bb.get() & 0xFF;
            }
        });
        try {
            another.deserialize(dis);
            return of(another);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return of();
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static MutableBitMap ofBytes(byte[] bytes) {
        return ofBytes(bytes, 0, bytes.length);
    }

    public static MutableBitMap ofBytes(byte[] bytes, int offset, int length) {
        return fromByteBuffer(ByteBuffer.wrap(bytes, offset, length));
    }

    @Override
    public void or(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return;
        }
        if (index instanceof RangeBitmap) {
            bitmap.add((long) ((RangeBitmap) index).start, ((RangeBitmap) index).end);
            return;
        }
        bitmap.or(extract(index));
    }

    @Override
    public void and(ImmutableBitMap index) {
        if (index.isEmpty()) {
            bitmap.clear();
            return;
        }
        if (index.isFull()) {
            return;
        }
        bitmap.and(extract(index));
    }

    @Override
    public void not(int bound) {
        bitmap.flip(0L, bound);
    }

    @Override
    public void andNot(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return;
        }
        if (index.isFull()) {
            bitmap.clear();
            return;
        }
        if (index instanceof RangeBitmap) {
            FasterAggregation.andNot(this, ((RangeBitmap) index));
            return;
        }
        bitmap.andNot(extract(index));
    }

    @Override
    public void add(int index) {
        bitmap.add(index);
    }

    @Override
    public void remove(int index) {
        bitmap.remove(index);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(bitmap.clone());
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ROARING_MUTABLE;
    }
}