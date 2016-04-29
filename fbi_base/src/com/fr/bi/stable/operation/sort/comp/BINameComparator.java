package com.fr.bi.stable.operation.sort.comp;

import com.fr.bi.base.BIName;
import com.fr.general.ComparatorUtils;

/**
 * Created by GUY on 2015/3/27.
 */
public class BINameComparator extends AbstractComparator<BIName> {

    @Override
    public int compare(BIName o1, BIName o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return ComparatorUtils.compare(o1.getValue().toUpperCase(), o2.getValue().toLowerCase());
    }
}