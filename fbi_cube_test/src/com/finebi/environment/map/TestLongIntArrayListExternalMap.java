package com.finebi.environment.map;

import com.finebi.cube.map.ExternalMap;
import com.finebi.cube.map.map2.LongIntArrayListExternalMap;
import com.finebi.tool.BITestConstants;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.array.IntList;
import com.fr.bi.stable.structure.array.IntListFactory;
import com.fr.stable.collections.array.IntArray;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang on 2016/9/2.
 */
public class TestLongIntArrayListExternalMap {
    public static void main(String[] args) {
        LongIntArrayListExternalMap map = new LongIntArrayListExternalMap(ComparatorFacotry.LONG_ASC, "test/Long");
        for (long c = 1; c < BITestConstants.MAPSIZE; c++) {
            IntList list = IntListFactory.createIntList();
            for (int i = BITestConstants.GAP-1; i > 0; i--) {
                list.add((int) (i * c));
                list.add((int) (i * c + BITestConstants.GAP));
            }
            map.put(c, list);
        }

        Iterator<ExternalMap.Entry<Long, IntList>> it = map.getIterator();
        while (it.hasNext()) {
            Map.Entry<Long, IntList> entry = it.next();
            entry.getKey();
        }
//        System.err.println(map.size());
    }
}
