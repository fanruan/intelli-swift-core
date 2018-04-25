package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BIDesignConstants.DESIGN.SORT;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;

/**
 * Created by pony on 2017/12/21.
 */
public class SortAdaptor {
    public static Sort transformSort(int sortType) {
        switch (sortType) {
            case ConfConstant.SortType.ASC:
                return new AscSort(0);
            default:
                return new DescSort(0);
        }
    }

    public static Sort adaptorDimensionSort(FineDimensionSort dimensionSort, int index) {
        if (dimensionSort == null) {
            return new AscSort(index);
        }
        switch (dimensionSort.getType()) {
            case SORT.ASC:
            case SORT.FILTER_ASC:
            case SORT.NUMBER_ASC:
                return new AscSort(index);
            case SORT.DESC:
            case SORT.FILTER_DESC:
            case SORT.NUMBER_DESC:
                return new DescSort(index);
            default:
                return new NoneSort();
        }
    }
}
