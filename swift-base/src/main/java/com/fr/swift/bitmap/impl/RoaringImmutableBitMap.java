package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.log.SwiftLoggers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author anchore
 */
public class RoaringImmutableBitMap extends BaseRoaringBitMap {
    RoaringImmutableBitMap(MutableRoaringBitmap bitmap) {
        super(bitmap);
    }

    static ImmutableBitMap of(MutableRoaringBitmap bitmap) {
        return new RoaringImmutableBitMap(bitmap);
    }

    public static ImmutableBitMap of() {
        return of(new MutableRoaringBitmap());
    }

    private static ImmutableBitMap fromByteBuffer(final ByteBuffer bb) {
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
            return BitMaps.EMPTY_IMMUTABLE;
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    static ImmutableBitMap fromBytes(byte[] bytes) {
        return fromBytes(bytes, 0, bytes.length);
    }

    private static ImmutableBitMap fromBytes(byte[] bytes, int offset, int length) {
        return fromByteBuffer(ByteBuffer.wrap(bytes, offset, length));
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index.getType() == BitMapType.ALL_SHOW) {
            return this;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.and(extract(index));

        return of(copy);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index.getType() == BitMapType.ALL_SHOW) {
            return index;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.or(extract(index));

        return of(copy);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index.getType() == BitMapType.ALL_SHOW) {
            return BitMaps.EMPTY_IMMUTABLE;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.andNot(extract(index));

        return of(copy);
    }

    @Override
    public ImmutableBitMap getNot(int bound) {
        MutableRoaringBitmap copy = bitmap.clone();
        copy.flip(0L, bound);

        return of(copy);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(bitmap);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ROARING_IMMUTABLE;
    }
}
