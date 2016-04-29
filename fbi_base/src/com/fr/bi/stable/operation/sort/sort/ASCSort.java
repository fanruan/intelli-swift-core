package com.fr.bi.stable.operation.sort.sort;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.AbstractSort;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.IComparator;

/**
 * Created by GUY on 2015/4/9.
 */
public class ASCSort extends AbstractSort {

    protected transient IComparator comparator = ComparatorFacotry.getComparator(getSortType());

    @Override
    public int getSortType() {
        return BIReportConstant.SORT.ASC;
    }

    @Override
    public IComparator getComparator() {
        return comparator;
    }
}