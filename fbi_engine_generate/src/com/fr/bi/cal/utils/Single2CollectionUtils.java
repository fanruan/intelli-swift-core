package com.fr.bi.cal.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucifer on 2017-3-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class Single2CollectionUtils {

    public static <T> Set toSet(T single) {
        Set<T> set = new HashSet<T>();
        set.add(single);
        return set;
    }

    public static <T> List toList(T single) {
        List<T> list = new ArrayList<T>();
        list.add(single);
        return list;
    }

    public static <T> List setToList(Set<T> set) {
        List<T> list = new ArrayList<T>();
        list.addAll(set);
        return list;
    }
}
