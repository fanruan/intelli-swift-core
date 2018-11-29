package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author FineSoft
 * @date 2015/7/14
 */
public abstract class BaseIntListExternalMap<K> extends ExternalMap<K, IntList> {
    public BaseIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public BaseIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    public BaseIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath, boolean isKeepDiskFile) {
        super(bufferSize, comparator, dataFolderAbsPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<K, IntList> getMemMapIO(TreeMap<K, IntList> currentContainer) {
        return new MemIntExternalMapIO<K>(currentContainer);
    }

    public void put(K key, int row) {
        IntList old = this.get(key);
        if (old == null) {
            old = IntListFactory.createIntList();
            old.add(row);
            put(key, old);
        } else {
            old.add(row);
            increaseValueSize();
        }
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