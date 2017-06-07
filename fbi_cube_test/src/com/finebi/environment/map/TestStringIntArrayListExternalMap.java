package com.finebi.environment.map;

import com.finebi.cube.map.ExternalMap;
import com.finebi.cube.map.map2.StringIntArrayListExternalMap;
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
public class TestStringIntArrayListExternalMap {
    public static void main(String[] args) {
        StringIntArrayListExternalMap map = new StringIntArrayListExternalMap(ComparatorFacotry.CHINESE_ASC, "test/String");
        for (int c = 1; c < BITestConstants.MAPSIZE; c++) {
            IntList list = IntListFactory.createIntList();
            for (int i = BITestConstants.GAP-1; i > 0; i--) {
                list.add(i * c);
                list.add(i * c + BITestConstants.GAP);
            }
            map.put(c + "haha", list);
        }

        Iterator<ExternalMap.Entry<String, IntList>> it = map.getIterator();
        while (it.hasNext()) {
            Map.Entry<String, IntList> entry = it.next();
//            System.out.println(entry.getKey());
        }
//        System.err.println(map.size());
    }
}
