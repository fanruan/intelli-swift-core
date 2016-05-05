package com.fr.bi.stable.io.sortlist;

import com.fr.bi.stable.io.newio.NIOReader;

/**
 * Created by 小灰灰 on 14-1-8.
 */
public class SortNIOReadIntList implements ISortNIOReadList<Integer> {
    private static final Integer ZERO = new Integer(0);
    private final NIOIntLookup LOOKUP = new NIOIntLookup();
    private NIOReader<Integer> irml;
    private int size;

    public SortNIOReadIntList(NIOReader<Integer> irml, long groupCount) {
        this.irml = irml;
        size = (int) groupCount;
    }

    @Override
    public int[] indexOf(Integer[] values) {
        return ArrayLookupHelper.lookup(values, LOOKUP);
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * 释放资源
     */
    @Override
    public void releaseResource() {
    }

    @Override
    public Integer get(long index) {
        return irml.get(index);
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }

    @Override
    public Integer[] createKey(int length) {
        return new Integer[length];
    }

    @Override
    public Integer createValue(Object v) {
        return v == null ? ZERO : Integer.valueOf(v.toString());
    }

    private final class NIOIntLookup implements ArrayLookupHelper.Lookup<Integer> {

        @Override
        public Integer lookupByIndex(int index) {
            return SortNIOReadIntList.this.irml.get(index);
        }

        @Override
        public int minIndex() {
            return 0;
        }

        @Override
        public int maxIndex() {
            return SortNIOReadIntList.this.size - 1;
        }

        @Override
        public int compare(Integer t1, Integer t2) {
            return t1.compareTo(t2);
        }
    }
}