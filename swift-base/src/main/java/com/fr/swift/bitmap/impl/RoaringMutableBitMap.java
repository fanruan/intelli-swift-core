package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
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
public class RoaringMutableBitMap extends BaseRoaringBitMap implements MutableBitMap {
    private RoaringMutableBitMap(MutableRoaringBitmap bitmap) {
        super(bitmap);
    }

    private static MutableBitMap newInstance(MutableRoaringBitmap bitmap) {
        return new RoaringMutableBitMap(bitmap);
    }

    public static MutableBitMap newInstance() {
        return newInstance(new MutableRoaringBitmap());
    }

    public static MutableBitMap fromByteBuffer(final ByteBuffer bb) {
        MutableRoaringBitmap another = new MutableRoaringBitmap();
        DataInputStream dis = new DataInputStream(new InputStream() {
            @Override
            public int read() {
                return bb.get() & 0xFF;
            }
        });
        try {
            another.deserialize(dis);
            return newInstance(another);
        } catch (IOException e) {
            SwiftLoggers.getLogger(RoaringMutableBitMap.class).error(e);
            return newInstance();
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                SwiftLoggers.getLogger(RoaringMutableBitMap.class).error(e);
            }
        }
    }

    public static MutableBitMap fromBytes(byte[] bytes) {
        return fromBytes(bytes, 0, bytes.length);
    }

    public static MutableBitMap fromBytes(byte[] bytes, int offset, int length) {
        return fromByteBuffer(ByteBuffer.wrap(bytes, offset, length));
    }

    @Override
    public void or(MutableBitMap index) {
        bitmap.or(extract(index));
    }

    @Override
    public void and(MutableBitMap index) {
        bitmap.and(extract(index));
    }

    @Override
    public void andNot(MutableBitMap index) {
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
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return clone();
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.and(extract(index));

        return RoaringImmutableBitMap.newInstance(copy);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return index;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.or(extract(index));

        return RoaringImmutableBitMap.newInstance(copy);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index instanceof AllShowBitMap) {
            return BitMaps.EMPTY_IMMUTABLE;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.andNot(extract(index));

        return RoaringImmutableBitMap.newInstance(copy);
    }

    @Override
    public ImmutableBitMap getNot(int rowCount) {
        MutableRoaringBitmap copy = bitmap.clone();
        copy.flip(0L, rowCount);

        return RoaringImmutableBitMap.newInstance(copy);
    }

    @Override
    public ImmutableBitMap clone() {
        return newInstance(bitmap.clone());
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ROARING_MUTABLE;
    }

}