package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
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
        if (dimensionSort == null){
            return new AscSort(index);
        }
        switch (dimensionSort.getType()) {
            case BIDesignConstants.DESIGN.SORT.ASC:
                return new AscSort(index);
            default:
                return new DescSort(index);
        }
    }
}
