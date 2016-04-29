package com.fr.bi.stable.operation.sort.comp;

import com.fr.general.ComparatorUtils;

/**
 * Created by GUY on 2015/3/11.
 */
public class ASCComparator extends AbstractComparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return ComparatorUtils.compare(o1, o2);
    }
}