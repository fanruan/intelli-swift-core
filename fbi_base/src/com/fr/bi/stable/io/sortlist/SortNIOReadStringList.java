package com.fr.bi.stable.io.sortlist;

import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.stable.StringUtils;

public class SortNIOReadStringList implements ISortNIOReadList<String> {
    private final NIOStringLookup LOOKUP = new NIOStringLookup();
    private NIOReader<String> drml;
    private int size;

    /**
     * 默认的构造函数
     */
    public SortNIOReadStringList(NIOReader<String> drml, long groupCount) {
        this.drml = drml;
        size = (int) groupCount;
    }

    @Override
    public String get(long row) {
        if (row < size) {
            return drml.get(row);
        }
        return null;
    }

    @Override
    public int[] indexOf(String[] values) {
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
    public String[] createKey(int length) {
        return new String[length];
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }

    @Override
    public String createValue(Object v) {
        return v == null ? StringUtils.EMPTY : v.toString();
    }

    private final class NIOStringLookup implements ArrayLookupHelper.Lookup<String> {

        @Override
        public String lookupByIndex(int index) {
            return SortNIOReadStringList.this.get(index);
        }

        @Override
        public int minIndex() {
            return 0;
        }

        @Override
        public int maxIndex() {
            return SortNIOReadStringList.this.size - 1;
        }

        @Override
        public int compare(String t1, String t2) {
            return ComparatorFacotry.CHINESE_ASC.compare(t1, t2);
        }
    }
}