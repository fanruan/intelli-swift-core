package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.IntConsumer;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

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
        IntIterator iterator = intIterator();
        while (iterator.hasNext()) {
            if (action.actionPerformed(iterator.nextInt())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {

            com.fr.swift.bitmap.roaringbitmap.IntIterator itr = bitmap.getIntIterator();

            @Override
            public int nextInt() {
                return itr.next();
            }

            @Override
            public boolean hasNext() {
                return itr.hasNext();
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
            IoUtil.close(baos);
        }
    }

    @Override
    public String toString() {
        return bitmap.toString();
    }
}