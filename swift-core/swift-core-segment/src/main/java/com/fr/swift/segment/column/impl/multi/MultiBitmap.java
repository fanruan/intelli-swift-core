package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AbstractBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.util.IoUtil;

import java.io.OutputStream;

/**
 * 只用于计算。序列化，clone啥的用到才给做（意思就是不做）
 * <p>
 * 只能和同一个表的MultiBitmap进行位运算
 *
 * @author anchore
 * @date 2019/7/12
 */
public class MultiBitmap extends AbstractBitMap {
    private ImmutableBitMap[] bitmaps;

    private int[] offsets;

    public MultiBitmap(ImmutableBitMap[] bitmaps, int[] offsets) {
        this.bitmaps = bitmaps;
        this.offsets = offsets;
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        MultiBitmap multiBitmap = (MultiBitmap) index;

        ImmutableBitMap[] andBitmaps = new ImmutableBitMap[bitmaps.length];
        for (int i = 0; i < andBitmaps.length; i++) {
            andBitmaps[i] = bitmaps[i].getAnd(multiBitmap.bitmaps[i]);
        }

        return new MultiBitmap(andBitmaps, offsets);
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        MultiBitmap multiBitmap = (MultiBitmap) index;

        ImmutableBitMap[] orBitmaps = new ImmutableBitMap[bitmaps.length];
        for (int i = 0; i < orBitmaps.length; i++) {
            orBitmaps[i] = bitmaps[i].getOr(multiBitmap.bitmaps[i]);
        }

        return new MultiBitmap(orBitmaps, offsets);
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        MultiBitmap multiBitmap = (MultiBitmap) index;

        ImmutableBitMap[] andNotBitmaps = new ImmutableBitMap[bitmaps.length];
        for (int i = 0; i < andNotBitmaps.length; i++) {
            andNotBitmaps[i] = bitmaps[i].getAndNot(multiBitmap.bitmaps[i]);
        }

        return new MultiBitmap(andNotBitmaps, offsets);
    }

    @Override
    public ImmutableBitMap getNot(int bound) {
        ImmutableBitMap[] notBitmaps = new ImmutableBitMap[bitmaps.length];
        for (int i = 0; i < notBitmaps.length; i++) {
            notBitmaps[i] = bitmaps[i].getNot(bound);
        }

        return new MultiBitmap(notBitmaps, offsets);
    }

    @Override
    public boolean contains(int index) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (index >= offsets[i] && index < offsets[i + 1]) {
                return bitmaps[i].contains(index - offsets[i]);
            }
        }
        return false;
    }

    @Override
    public void traversal(final TraversalAction action) {
        for (int i = 0; i < bitmaps.length; i++) {
            final int finalI = i;
            bitmaps[i].traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    action.actionPerformed(row + offsets[finalI]);
                }
            });
        }
    }

    @Override
    public boolean breakableTraversal(final BreakTraversalAction action) {
        boolean travelOver = true;
        for (int i = 0; i < bitmaps.length; i++) {
            final int finalI = i;
            travelOver &= bitmaps[i].breakableTraversal(new BreakTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    return action.actionPerformed(row + offsets[finalI]);
                }
            });
        }
        return travelOver;
    }

    @Override
    public int getCardinality() {
        int c = 0;
        for (ImmutableBitMap bitmap : bitmaps) {
            c += bitmap.getCardinality();
        }
        return c;
    }

    @Override
    public boolean isEmpty() {
        for (ImmutableBitMap bitmap : bitmaps) {
            if (!bitmap.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ImmutableBitMap clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BitMapType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterator intIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(OutputStream output) {
        IoUtil.close(output);
        throw new UnsupportedOperationException();
    }
}