package com.finebi.cube.map.map2;


import com.finebi.cube.map.ExternalMap;
import com.fr.stable.collections.array.IntArray;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/14.
 */
public abstract class IntArrayListExternalMap<K> extends ExternalMap<K, IntArray> {
    public IntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public IntArray combineValue(TreeMap<Integer, IntArray> list) {
        IntArray result = new IntArray(1);
        Iterator<Map.Entry<Integer, IntArray>> it = list.entrySet().iterator();
        boolean flag = true;
        while (it.hasNext()) {
            if (flag) {
                result = it.next().getValue();
                flag = false;
            } else {
                IntArray temp = it.next().getValue();
                for (int i = 0; i < temp.size; i++) {
                    result.add(temp.get(i));
                }
                it.remove();
            }
        }
        return result;
    }

}