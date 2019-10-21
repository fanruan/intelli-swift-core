package com.fr.swift.segment.operator.collate.segment.bitmap;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by lyon on 2019/2/20.
 */
public class BitMapShifterFactory {

    public static BitMapShifter create(int start, int rowCount, ImmutableBitMap allShow) {
        if (rowCount == allShow.getCardinality()) {
            return new IdBitMapShifter(start);
        }
        int deleted = rowCount - allShow.getCardinality();
        if (deleted <= Byte.MAX_VALUE) {
            return new ByteBitMapShifter(start, rowCount, allShow);
        } else if (deleted <= Short.MAX_VALUE) {
            return new ShortBitMapShifter(start, rowCount, allShow);
        } else {
            return new IntBitMapShifter(start, rowCount, allShow);
        }
    }

    private static abstract class ASTBitMapShifter implements BitMapShifter {

        private int start;
        private ImmutableBitMap allShow;

        public ASTBitMapShifter(int start, ImmutableBitMap allShow) {
            this.start = start;
            this.allShow = allShow;
        }

        abstract void setDeletedRowCount(int row, int count);

        abstract int getDeletedRowCount(int row);

        void init() {
            final int[] count = new int[1];
            final int[] prev = new int[]{-1};
            allShow.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    count[0] += row - prev[0] - 1;
                    setDeletedRowCount(row, count[0]);
                    prev[0] = row;
                }
            });
        }

        @Override
        public ImmutableBitMap shift(RowTraversal original) {
            final MutableBitMap bitMap = BitMaps.newRoaringMutable();
            original.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    bitMap.add(row + start - getDeletedRowCount(row));
                }
            });
            return bitMap;
        }
    }

    private static class IntBitMapShifter extends ASTBitMapShifter {

        private int[] deleted;

        public IntBitMapShifter(int start, int rowCount, ImmutableBitMap allShow) {
            super(start, allShow);
            this.deleted = new int[rowCount];
            init();
        }

        @Override
        void setDeletedRowCount(int row, int count) {
            deleted[row] = count;
        }

        @Override
        int getDeletedRowCount(int row) {
            return deleted[row];
        }
    }

    private static class ShortBitMapShifter extends ASTBitMapShifter {

        private short[] deleted;

        public ShortBitMapShifter(int start, int rowCount, ImmutableBitMap allShow) {
            super(start, allShow);
            this.deleted = new short[rowCount];
            init();
        }

        @Override
        void setDeletedRowCount(int row, int count) {
            deleted[row] = (short) count;
        }

        @Override
        int getDeletedRowCount(int row) {
            return deleted[row];
        }
    }

    private static class ByteBitMapShifter extends ASTBitMapShifter {

        private byte[] deleted;

        public ByteBitMapShifter(int start, int rowCount, ImmutableBitMap allShow) {
            super(start, allShow);
            this.deleted = new byte[rowCount];
            init();
        }

        @Override
        void setDeletedRowCount(int row, int count) {
            deleted[row] = (byte) count;
        }

        @Override
        int getDeletedRowCount(int row) {
            return deleted[row];
        }
    }

    private static class IdBitMapShifter implements BitMapShifter {

        private int start;

        public IdBitMapShifter(int start) {
            this.start = start;
        }

        @Override
        public ImmutableBitMap shift(RowTraversal original) {
            final MutableBitMap bitMap = BitMaps.newRoaringMutable();
            original.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    bitMap.add(row + start);
                }
            });
            return bitMap;
        }
    }
}
