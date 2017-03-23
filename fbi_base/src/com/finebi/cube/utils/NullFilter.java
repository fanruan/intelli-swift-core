package com.finebi.cube.utils;

import java.util.*;

/**
 * Created by Lucifer on 2017-3-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class NullFilter {

    public static <T> Collection<T> filterCollection(Collection<T> set) {
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null) {
                it.remove();
            }
        }
        return set;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<String>();
        List<String> list = new ArrayList<String>();
        set.add("1");
        set.add("2");
        set.add(null);
        set.add("3");
        set.add(null);

        list.add("1");
        list.add("2");
        list.add(null);
        list.add("3");
        list.add(null);

        System.out.println(set.size());
        System.out.println(list.size());

        list = (List<String>) filterCollection(list);
        System.out.println(filterCollection(set).size());
        System.out.println(list.size());
    }
}
