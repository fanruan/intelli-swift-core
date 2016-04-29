package com.fr.bi.stable.operation.sort.sort;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.AbstractSort;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.IComparator;

/**
 * Created by Young's on 2016/3/3.
 */
public class NumberASCSort extends AbstractSort {

    protected transient IComparator comparator = ComparatorFacotry.getComparator(getSortType());

    @Override
    public int getSortType() {
        return BIReportConstant.SORT.NUMBER_ASC;
    }

    @Override
    public IComparator getComparator() {
        return comparator;
    }
}
