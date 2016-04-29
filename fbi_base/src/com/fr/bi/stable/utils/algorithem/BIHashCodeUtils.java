package com.fr.bi.stable.utils.algorithem;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Connery on 2015/12/4.
 */
public class BIHashCodeUtils {
    /**
     * 数组的hash值
     *
     * @param array array数组
     * @return hash值
     */
    public static int hashCode(Object[] array) {
        int prime = 31;
        if (array == null || array.length == 0) {
            return 0;
        }
        int result = 1;
        for (Object anArray : array) {
            result = prime * result
                    + (anArray == null ? 0 : anArray.hashCode());
        }
        return result;
    }

    public static int hashCode(Collection collection) {
        int result = 1, prime = 31;
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            result = result * prime + it.next().hashCode();
        }
        return result;
    }
}