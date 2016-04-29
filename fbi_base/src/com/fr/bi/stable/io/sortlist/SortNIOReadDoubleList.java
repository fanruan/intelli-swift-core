package com.fr.bi.stable.io.sortlist;

import com.fr.bi.stable.io.newio.NIOReader;

public class SortNIOReadDoubleList implements ISortNIOReadList<Double> {

    private static final Double ZERO = new Double(0);
    private final NIODoubleLookup LOOKUP = new NIODoubleLookup();
    private NIOReader<Double> drml;
    private int size;

    /**
     * 默认的构造函数，生成一个容量为8的整数链表
     */
    public SortNIOReadDoubleList(NIOReader<Double> drml, long groupCount) {
        this.drml = drml;
        size = (int) groupCount;
    }

    @Override
    public int[] indexOf(Double[] values) {
        return ArrayLookupHelper.lookup(values, LOOKUP);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
    }

    @Override
    public Double get(long index) {
        return drml.get(index);
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }

    @Override
    public Double[] createKey(int length) {
        return new Double[length];
    }

    @Override
    public Double createValue(Object v) {
        return v == null ? ZERO : Double.valueOf(v.toString());
    }

    private final class NIODoubleLookup implements ArrayLookupHelper.Lookup<Double> {

        @Override
        public Double lookupByIndex(int index) {
            return SortNIOReadDoubleList.this.drml.get(index);
        }

        @Override
        public int minIndex() {
            return 0;
        }

        @Override
        public int maxIndex() {
            return SortNIOReadDoubleList.this.size - 1;
        }

        @Override
        public int compare(Double t1, Double t2) {
            return t1.compareTo(t2);
        }
    }
}