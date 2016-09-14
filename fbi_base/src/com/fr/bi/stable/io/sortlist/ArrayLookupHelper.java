package com.fr.bi.stable.io.sortlist;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.stable.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

public class ArrayLookupHelper {

    public static <T> int[] lookup(T[] values2Lookup, Lookup<T> lookup) {
        Integer[] indicesOfValues2Lookup = new Integer[values2Lookup.length];
        for (int i = 0; i < indicesOfValues2Lookup.length; i++) {
            indicesOfValues2Lookup[i] = i;
        }

        return lookup(values2Lookup, indicesOfValues2Lookup, lookup.minIndex(), lookup.maxIndex(), lookup);
    }

    private static <T> int[] lookup(T[] values2Lookup, Integer[] indicesOfValues2Lookup,
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
        IntList alist = new IntList();
        IntList indicesOfValues2CheckOfLowInterval = new IntList(indicesOfValues2Lookup.length);
        IntList blist = new IntList();
        IntList indicesOfValues2CheckOfHighInterval = new IntList(indicesOfValues2Lookup.length);
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
        Integer[] indicesOfValues2CheckOfLowIntervalArray = indicesOfValues2CheckOfLowInterval.toArray();
        if (indicesOfValues2CheckOfLowIntervalArray.length > 0) {
            int[] lowIndices = lookup(values2Lookup, indicesOfValues2CheckOfLowIntervalArray, low, mid - 1, lookup);
            for (int i = 0; i < indicesOfValues2CheckOfLowIntervalArray.length; i++) {
                res[blist.get(i)] = lowIndices[i];
            }
        }
        Integer[] indicesOfValues2CheckOfHighIntervalArray = indicesOfValues2CheckOfHighInterval.toArray();
        if (indicesOfValues2CheckOfHighIntervalArray.length > 0) {
            int[] highIndices = lookup(values2Lookup, indicesOfValues2CheckOfHighIntervalArray, mid + 1, high, lookup);
            for (int i = 0; i < indicesOfValues2CheckOfHighIntervalArray.length; i++) {
                res[alist.get(i)] = highIndices[i];
            }
        }
        return res;
    }
    public static int getStartIndex4StartWith(ICubeColumnIndexReader reader, String kw, Comparator comparator) {
        if (StringUtils.isEmpty(kw)){
            return 0;
        }
        return getStartIndex4StartWith(reader, 0, reader.sizeOfGroup() - 1, kw, comparator);
    }

    public static int getEndIndex4StartWith(ICubeColumnIndexReader reader, String kw, Comparator comparator) {
        if (StringUtils.isEmpty(kw)){
            return reader.sizeOfGroup();
        }
        return getEndIndex4StartWith(reader, 0, reader.sizeOfGroup() - 1, kw, comparator);
    }


    private static String getString(ICubeColumnIndexReader reader, int index) {
        Object ob = reader.getGroupValue(index);
        return ob == null ? StringUtils.EMPTY : ob.toString();
    }

    private static int getStartIndex4StartWith(ICubeColumnIndexReader reader, int start, int end, String kw, Comparator<String> comparator){
        int mid = ((start + end)>>1);
        String v = getString(reader, mid);
        int result = comparator.compare(v, kw);
        if (result == 0){
            return mid;
        }
        if (v.startsWith(kw)){
            if (mid == start){
                return mid;
            }
            if (getString(reader, mid - 1).startsWith(kw)){
                return getStartIndex4StartWith(reader, start, mid, kw, comparator);
            } else {
                return mid;
            }
        } else  if (mid == start ){
            return getString(reader, end).startsWith(kw) ? end : -1;
        }
        if (result > 0){
            return getStartIndex4StartWith(reader, start, mid, kw, comparator);
        } else {
            return getStartIndex4StartWith(reader, mid, end, kw, comparator);
        }
    }

    private static int getEndIndex4StartWith(ICubeColumnIndexReader reader, int start, int end, String kw, Comparator<String> comparator){
        int mid = ((start + end)>>1);
        String v = getString(reader, mid);
        int result = comparator.compare(v, kw);
        if (result == 0 || v.startsWith(kw)){
            return mid == start ? (getString(reader, end).startsWith(kw)? end : mid) :getEndIndex4StartWith(reader, mid, end, kw, comparator);
        } else if (mid == start){
            return getString(reader, end).startsWith(kw)? end : -1;
        }
        if (result > 0){
            return getEndIndex4StartWith(reader, start, mid, kw, comparator);
        } else {
            return getEndIndex4StartWith(reader, mid, end, kw, comparator);
        }
    }


    public static interface Lookup<T> {
        public int minIndex();

        public int maxIndex();

        public T lookupByIndex(int index);

        public int compare(T t1, T t2);
    }
}