package com.fr.bi.stable.io.sortlist;

import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Arrays;

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

    public static interface Lookup<T> {
        public int minIndex();

        public int maxIndex();

        public T lookupByIndex(int index);

        public int compare(T t1, T t2);
    }
}