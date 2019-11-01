package com.fr.swift.util;

import com.fr.swift.structure.array.HeapIntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.Arrays;

public class ArrayLookupHelper {

    public static <T> int[] lookup(T[] values2Lookup, Lookup<T> lookup) {
        int[] indicesOfValues2Lookup = new int[values2Lookup.length];
        for (int i = 0; i < indicesOfValues2Lookup.length; i++) {
            indicesOfValues2Lookup[i] = i;
        }

        return lookup(values2Lookup, indicesOfValues2Lookup, lookup.minIndex(), lookup.maxIndex(), lookup);
    }

    private static <T> int[] lookup(T[] values2Lookup, int[] indicesOfValues2Lookup,
                                    int low, int high, Lookup<T> lookup) {
        if (indicesOfValues2Lookup.length == 0) {
            return new int[0];
        }
        int[] res = new int[indicesOfValues2Lookup.length];
        Arrays.fill(res, -1);
        if (low > high) {
            return res;
        }
        int mid = ((low + high) >> 1);
        T midValue = lookup.lookupByIndex(mid);
        HeapIntList alist = IntListFactory.createHeapIntList();
        HeapIntList indicesOfValues2CheckOfLowInterval = IntListFactory.createHeapIntList(indicesOfValues2Lookup.length);
        HeapIntList blist = IntListFactory.createHeapIntList();
        HeapIntList indicesOfValues2CheckOfHighInterval = IntListFactory.createHeapIntList(indicesOfValues2Lookup.length);
        for (int vi = 0; vi < indicesOfValues2Lookup.length; vi++) {
            int compareResult = lookup.compare(values2Lookup[indicesOfValues2Lookup[vi]], midValue);
            if (compareResult > 0) {
                if (low != high) {
                    indicesOfValues2CheckOfHighInterval.add(indicesOfValues2Lookup[vi]);
                    alist.add(vi);
                }
            } else if (compareResult < 0) {
                if (low != high) {
                    indicesOfValues2CheckOfLowInterval.add(indicesOfValues2Lookup[vi]);
                    blist.add(vi);
                }
            } else {
                res[vi] = mid;
            }
        }
        int[] indicesOfValues2CheckOfLowIntervalArray = indicesOfValues2CheckOfLowInterval.toArray();
        if (indicesOfValues2CheckOfLowIntervalArray.length > 0) {
            int[] lowIndices = lookup(values2Lookup, indicesOfValues2CheckOfLowIntervalArray, low, mid - 1, lookup);
            for (int i = 0; i < indicesOfValues2CheckOfLowIntervalArray.length; i++) {
                res[blist.get(i)] = lowIndices[i];
            }
        }
        int[] indicesOfValues2CheckOfHighIntervalArray = indicesOfValues2CheckOfHighInterval.toArray();
        if (indicesOfValues2CheckOfHighIntervalArray.length > 0) {
            int[] highIndices = lookup(values2Lookup, indicesOfValues2CheckOfHighIntervalArray, mid + 1, high, lookup);
            for (int i = 0; i < indicesOfValues2CheckOfHighIntervalArray.length; i++) {
                res[alist.get(i)] = highIndices[i];
            }
        }
        return res;
    }

    public static int getStartIndex4StartWith(Lookup lookup, String kw) {
        if (Strings.isEmpty(kw)) {
            return 0;
        }
        return getStartIndex4StartWith(lookup, lookup.minIndex(), lookup.maxIndex(), kw);
    }

    public static int getEndIndex4StartWith(Lookup lookup, String kw) {
        if (Strings.isEmpty(kw)) {
            return lookup.maxIndex() + 1;
        }
        return getEndIndex4StartWith(lookup, lookup.minIndex(), lookup.maxIndex(), kw);
    }


    private static String getString(Lookup lookup, int index) {
        Object ob = lookup.lookupByIndex(index);
        return ob == null ? Strings.EMPTY : ob.toString();
    }

    private static int getStartIndex4StartWith(Lookup lookup, int start, int end, String kw) {
        int mid = ((start + end) >> 1);
        String v = getString(lookup, mid);
        int result = lookup.compare(v, kw);
        if (result == 0) {
            return mid;
        }
        if (v.startsWith(kw)) {
            if (mid == start) {
                return mid;
            }
            if (getString(lookup, mid - 1).startsWith(kw)) {
                return getStartIndex4StartWith(lookup, start, mid, kw);
            } else {
                return mid;
            }
        } else if (mid == start) {
            return getString(lookup, end).startsWith(kw) ? end : -1;
        }
        if (result > 0) {
            return getStartIndex4StartWith(lookup, start, mid, kw);
        } else {
            return getStartIndex4StartWith(lookup, mid, end, kw);
        }
    }

    private static int getEndIndex4StartWith(Lookup lookup, int start, int end, String kw) {
        int mid = ((start + end) >> 1);
        String v = getString(lookup, mid);
        int result = lookup.compare(v, kw);
        if (result == 0 || v.startsWith(kw)) {
            return mid == start ? (getString(lookup, end).startsWith(kw) ? end : mid) : getEndIndex4StartWith(lookup, mid, end, kw);
        } else if (mid == start) {
            return getString(lookup, end).startsWith(kw) ? end : -1;
        }
        if (result > 0) {
            return getEndIndex4StartWith(lookup, start, mid, kw);
        } else {
            return getEndIndex4StartWith(lookup, mid, end, kw);
        }
    }


    /**
     * 返回2分查找的结果，如果不存在，就返回false与上一个的位置
     *
     * @param lookup
     * @param value
     * @return
     */
    public static <T> MatchAndIndex binarySearch(Lookup<T> lookup, T value) {
        return binarySearch(lookup, lookup.minIndex(), lookup.maxIndex(), value);
    }

    private static <T> MatchAndIndex binarySearch(Lookup<T> lookup, int start, int end, T value) {
        if (start > end) {
            return new MatchAndIndex(false, -1);
        }
        int mid = ((start + end) >> 1);
        int result = lookup.compare(lookup.lookupByIndex(mid), value);
        if (result == 0) {
            return new MatchAndIndex(true, mid);
        }
        //如果开始与结尾只相等，判断下start的位置即可。
        if (end == start) {
            return new MatchAndIndex(false, result < 0 ? start : start - 1);
        }
        if (result > 0) {
            return binarySearch(lookup, start, mid, value);
        } else {
            return binarySearch(lookup, mid + 1, end, value);
        }
    }

    public interface Lookup<T> {
        int minIndex();

        int maxIndex();

        T lookupByIndex(int index);

        int compare(T t1, T t2);
    }
}
