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
}
