package com.fr.swift.structure.external.map.intlist.map2;


import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/14.
 */
public abstract class IntArrayListExternalMap<K> extends ExternalMap<K, IntList> {
    public IntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    public IntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath, boolean isKeepDiskFile) {
        super(bufferSize, comparator, dataFolderAbsPath, isKeepDiskFile);
    }

    @Override
    public IntList combineValue(TreeMap<Integer, IntList> list) {
        IntList result = IntListFactory.createIntList(1);
        Iterator<Map.Entry<Integer, IntList>> it = list.entrySet().iterator();
        boolean flag = true;
        while (it.hasNext()) {
            if (flag) {
                result.clear();
                result = it.next().getValue();
                flag = false;
            } else {
                IntList temp = it.next().getValue();
                for (int i = 0; i < temp.size(); i++) {
                    result.add(temp.get(i));
                }
                temp.clear();
                it.remove();
            }
        }
        return result;
    }

}