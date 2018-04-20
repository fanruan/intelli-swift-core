package com.fr.swift.result.node.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/19.
 */
public class IteratorUtils {

    public static <T> List<T> iterator2List(Iterator<T> iterator) {
        List<T> list = new ArrayList<T>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
}
