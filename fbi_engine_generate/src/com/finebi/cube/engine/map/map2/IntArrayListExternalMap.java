package com.finebi.cube.engine.map.map2;


import com.finebi.cube.engine.map.ExternalMap;
import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.bi.stable.structure.collection.list.IntArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/14.
 */
public abstract class IntArrayListExternalMap<K> extends ExternalMap<K, IntArrayList> {
    public IntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public IntArrayList combineValue(TreeMap<Integer, IntArrayList> list) {
        IntArrayList result = new IntArrayList(1);
        Iterator<Map.Entry<Integer, IntArrayList>> it = list.entrySet().iterator();
        boolean flag = true;
        while (it.hasNext()) {
            if (flag) {
                result = it.next().getValue();
                flag = false;
            } else {
                IntArrayList temp = it.next().getValue();
                for (int i = 0; i < temp.size(); i++) {
                    result.add(temp.get(i));
                }
                it.remove();
            }
        }
        return result;
    }

}