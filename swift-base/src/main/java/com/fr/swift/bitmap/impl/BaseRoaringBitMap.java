package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.IntConsumer;
import com.fr.swift.bitmap.roaringbitmap.IntIterator;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.log.SwiftLoggers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author anchore
 */
public abstract class BaseRoaringBitMap extends AbstractBitMap {
    final MutableRoaringBitmap bitmap;

    BaseRoaringBitMap(MutableRoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    static MutableRoaringBitmap extract(ImmutableBitMap immutableMap) {
        if (immutableMap instanceof BaseRoaringBitMap) {
            return ((BaseRoaringBitMap) immutableMap).bitmap;
        }

        final MutableRoaringBitmap mutableRoaringBitmap = new MutableRoaringBitmap();
        if (immutableMap instanceof RangeBitmap) {
            RangeBitmap bitmap = (RangeBitmap) immutableMap;
            mutableRoaringBitmap.add((long) bitmap.start, bitmap.end);
            return mutableRoaringBitmap;
        }

        immutableMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                mutableRoaringBitmap.add(row);
            }
        });
        return mutableRoaringBitmap;
    }

    @Override
    public boolean contains(int index) {
        return bitmap.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return bitmap.isEmpty();
    }

    @Override
    public void traversal(final TraversalAction action) {
        bitmap.forEach(new IntConsumer() {
            @Override
            public void accept(int row) {
                action.actionPerformed(row);
            }
        });
    }

    @Override
    public boolean breakableTraversal(final BreakTraversalAction action) {
        IntIterator iterator = bitmap.getIntIterator();
        while (iterator.hasNext()) {
            if (action.actionPerformed(iterator.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCardinality() {
        return bitmap.getCardinality();
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.serialize(new DataOutputStream(baos));
            return baos.toByteArray();
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return new byte[0];
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    @Override
    public String toString() {
        return bitmap.toString();
    }
}