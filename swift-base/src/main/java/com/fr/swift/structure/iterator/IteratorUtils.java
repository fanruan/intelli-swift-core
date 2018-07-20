package com.fr.swift.structure.iterator;

import com.fr.swift.util.function.Function;

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

    @SuppressWarnings("unchecked")
    public static <PARAM, RETURN> RETURN[] list2Array(List<PARAM> list, Function<PARAM, RETURN> fn) {
        Object[] arr = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = fn.apply(list.get(i));
        }
        return (RETURN[]) arr;
    }

    public static <T> Iterator<T> emptyIterator() {
        return new ArrayList<T>(0).iterator();
    }
}
