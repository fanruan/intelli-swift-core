package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.LongList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/12/2.
 */
public abstract class LongListExternalMap <K> extends ExternalMap<K, LongList>{
    public LongListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public LongListExternalMap(Long containerSize, Comparator comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    public LongListExternalMap(Comparator comparator) {
        super(comparator);
    }


    @Override
    public LongList combineValue(TreeMap<Integer, LongList> list) {
        LongList result = new LongList(1);
        Iterator<Map.Entry<Integer, LongList>> it = list.entrySet().iterator();
        boolean flag = true;
        while (it.hasNext()) {
            if (flag) {
                result = it.next().getValue();
                flag = false;
            } else {
                LongList temp = it.next().getValue();
                for (int i = 0; i < temp.size(); i++) {
                    result.add(temp.get(i));
                }
                it.remove();
            }
        }
        return result;
    }
}