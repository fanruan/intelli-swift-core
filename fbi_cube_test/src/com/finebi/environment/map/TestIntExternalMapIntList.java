package com.finebi.environment.map;

import com.finebi.cube.map.ExternalMap;
import com.finebi.cube.map.IntegerIntListExternalMap;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang on 2016/8/30.
 */
public class TestIntExternalMapIntList {
    public static void main(String[] args) {
        IntegerIntListExternalMap map = new IntegerIntListExternalMap(ComparatorFacotry.INTEGER_ASC, "test");
        int listSize = 405000;
        int gap = 4;
        for (int c = 1; c < gap; c++) {
            IntList list = new IntList();
            for (int i = listSize; i > 0; i--) {
                list.add(i * c);
                list.add(i * c + 4);
            }
            map.put(c, list);
        }

        Iterator<ExternalMap.Entry<Integer, IntList>> it = map.getIterator();
        while (it.hasNext()) {
            Map.Entry<Integer, IntList> entry = it.next();
            entry.getKey();
        }
//        System.err.println(map.size());
    }
}
