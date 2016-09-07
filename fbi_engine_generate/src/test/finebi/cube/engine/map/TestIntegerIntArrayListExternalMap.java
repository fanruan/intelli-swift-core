package test.finebi.cube.engine.map;

import com.finebi.cube.engine.map.ExternalMap;
import com.finebi.cube.engine.map.map2.IntegerIntArrayListExternalMap;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.list.IntArrayList;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang on 2016/9/2.
 */
public class TestIntegerIntArrayListExternalMap {
    public static void main(String[] args) {
        IntegerIntArrayListExternalMap map = new IntegerIntArrayListExternalMap(ComparatorFacotry.INTEGER_ASC,"test/Integer");
        for (int c = 1; c < 405000; c++) {
            IntArrayList list = new IntArrayList();
            for (int i = 3; i >0; i--) {
                list.add(i*c);
                list.add(i*c+4);
            }
            map.put(c,list);
        }

        Iterator<ExternalMap.Entry<Integer, IntArrayList>> it = map.getIterator();
        while (it.hasNext()) {
            Map.Entry<Integer, IntArrayList> entry = it.next();
            System.out.println(entry.getKey());
        }
        System.err.println(map.size());
    }
}
