package com.finebi.cube.engine.map.mem;


import com.finebi.cube.engine.map.ExternalMap;
import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/14.
 */
public  class MyIntListExternalMap<K> extends ExternalMap<K, IntList> {
    public MyIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO<K, IntList> getExternalMapIO(String id_filePath) {
        return null;
    }

    @Override
    public ExternalMapIO<K, IntList> getMemMapIO(TreeMap<K, IntList> currentContainer) {
        return null;
    }

    public MyIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public IntList combineValue(TreeMap<Integer, IntList> list) {
        IntList result = new IntList(1);
        Iterator<Map.Entry<Integer, IntList>> it = list.entrySet().iterator();
        boolean flag = true;
        while (it.hasNext()) {
            if (flag) {
                result = it.next().getValue();
                flag = false;
            } else {
                IntList temp = it.next().getValue();
                for (int i = 0; i < temp.size(); i++) {
                    result.add(temp.get(i));
                }
                it.remove();
            }
        }
        return result;
    }
}