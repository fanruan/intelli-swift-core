package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static ImmutableBitMap ofStream(InputStream input) {
        MutableRoaringBitmap another = new MutableRoaringBitmap();
        DataInputStream dis = new DataInputStream(input);
        try {
            another.deserialize(dis);
            return of(another);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return BitMaps.EMPTY_IMMUTABLE;
        } finally {
            IoUtil.close(dis);
        }
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return index;
        }
        if (index.isFull()) {
            return this;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.and(extract(index));

        return of(copy);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return this;
        }
        if (index.isFull()) {
            return index;
        }

        MutableRoaringBitmap copy = bitmap.clone();
        copy.or(extract(index));

        return of(copy);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return this;
        }
        if (index.isFull()) {
            return BitMaps.EMPTY_IMMUTABLE;
        }
        if (index instanceof RangeBitmap) {
            return FasterAggregation.andNot(this, ((RangeBitmap) index));
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
