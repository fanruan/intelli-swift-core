package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.*;

/**
 * Created by FineSoft on 2015/7/14.
 */
public abstract class IntListExternalMap<K> extends ExternalMap<K, IntList> {
    public IntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    //    public static   void scombineValue(IntList body, IntList padding) {
//
//        ArrayList<Integer> result = new ArrayList<Integer>();
//
//        for (int i = 0; i < padding.size(); i++) {
//            result.add(padding.get(i));
//        }
//        for (int i = 0; i < body.size(); i++) {
//            result.add(body.get(i));
//        }
//        Object[] temp = result.toArray();
//        Arrays.sort(temp);
//        body.clear();
//        for (int i = 0; i < temp.length; i++) {
//            body.add((Integer) temp[i]);
//        }
//
//    }
    public static void main(String[] args) {
//        IntList a = new IntList();
//        a.add(2);
//        a.add(4);
//        IntList b = new IntList();
//        b.add(1);
//        b.add(3);
//        IntListExternalMap.scombineValue(a,b);
//        for (int i = 0; i < a.size(); i++) {
//            System.out.println(a.get(i));
//        }
//        TreeMap<Integer, String> map = new TreeMap<Integer, String>(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1 - o2;
//            }
//        });
//
//        map.put(1, "abc");
//        map.put(4, "abc");
//        map.put(2, "abc");
//        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
//        System.out.printf(it.next().getKey().toString());
//        System.out.printf(it.next().getKey().toString());
//        System.out.printf(it.next().getKey().toString());


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