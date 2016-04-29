package com.fr.bi.stable.io.sortlist;

import com.fr.bi.stable.io.newio.NIOReader;

/**
 * Created by 小灰灰 on 14-1-8.
 */
public class SortNIOReadLongList implements ISortNIOReadList<Long> {
    private final static Long ZERO = new Long(0);
    private final NIOLongLookup LOOKUP = new NIOLongLookup();
    private NIOReader<Long> lrml;
    private int size;

    public SortNIOReadLongList(NIOReader<Long> nioReader, long groupCount) {
        this.lrml = nioReader;
        size = (int) groupCount;
    }

    @Override
    public int[] indexOf(Long[] values) {
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
    public void clear() {
    }

    @Override
    public Long get(long index) {
        return lrml.get(index);
    }

    @Override
    public Long[] createKey(int length) {

        return new Long[length];
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }

    @Override
    public Long createValue(Object v) {
        return v == null ? ZERO : Long.valueOf(v.toString());
    }

    private final class NIOLongLookup implements ArrayLookupHelper.Lookup<Long> {

        @Override
        public Long lookupByIndex(int index) {
            return SortNIOReadLongList.this.lrml.get(index);
        }

        @Override
        public int minIndex() {
            return 0;
        }

        @Override
        public int maxIndex() {
            return SortNIOReadLongList.this.size - 1;
        }

        @Override
        public int compare(Long t1, Long t2) {
            return t1.compareTo(t2);
        }
    }
}