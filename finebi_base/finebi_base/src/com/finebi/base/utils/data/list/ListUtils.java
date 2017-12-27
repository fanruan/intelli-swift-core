package com.finebi.base.utils.data.list;

import java.util.List;

/**
 * Created by andrew_asa on 2017/10/9.
 */
public class ListUtils {

    public static boolean isEmptyList(List list) {

        return list == null || list.isEmpty();
    }

    public static boolean isNotEmptyList(List list) {

        return list != null && !list.isEmpty();
    }

    public static <T> T removeLastElement(List<T> list) {

        if (isNotEmptyList(list)) {
            return list.remove(list.size() - 1);
        }
        return null;
    }

    public static <T> T getFirstElement(List<T> list) {

        if (isEmptyList(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T getLastElement(List<T> list) {

        if (isEmptyList(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static Object[] toArray(Object... args) {

        return args;
    }
}
