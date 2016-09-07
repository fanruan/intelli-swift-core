package test.finebi.cube.engine.map;

import com.finebi.cube.engine.map.ExternalMap;
import com.finebi.cube.engine.map.map2.IntegerIntArrayListExternalMap;
import com.finebi.cube.engine.map.map2.StringIntArrayListExternalMap;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.list.IntArrayList;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang on 2016/9/2.
 */
public class TestStringIntArrayListExternalMap {
    public static void main(String[] args) {
        StringIntArrayListExternalMap map = new StringIntArrayListExternalMap(ComparatorFacotry.CHINESE_ASC,"test/String");
        for (int c = 1; c < 40000; c++) {
            IntArrayList list = new IntArrayList();
            for (int i = 3; i >0; i--) {
                list.add(i*c);
                list.add(i*c+4);
            }
            map.put(c+"haha",list);
        }

        Iterator<ExternalMap.Entry<String, IntArrayList>> it = map.getIterator();
        while (it.hasNext()) {
            Map.Entry<String, IntArrayList> entry = it.next();
            System.out.println(entry.getKey());
        }
        System.err.println(map.size());
    }
}
