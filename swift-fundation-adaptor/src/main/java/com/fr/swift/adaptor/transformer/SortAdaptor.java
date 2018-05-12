package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BIDesignConstants.DESIGN.SORT;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.dashboard.widget.dimension.sort.DimensionFilterASCSort;
import com.finebi.conf.internalimp.dashboard.widget.dimension.sort.DimensionFilterDESCSort;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.utils.BusinessTableUtils;

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
            case SORT.NUMBER_ASC:
                return new AscSort(index);
            case SORT.FILTER_ASC: {
                String fieldId = ((DimensionFilterASCSort)dimensionSort).getValue().getTargetFieldId();
                return new AscSort(index, new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)));
            }
            case SORT.DESC:
            case SORT.NUMBER_DESC:
                return new DescSort(index);
            case SORT.FILTER_DESC: {
                String fieldId = ((DimensionFilterDESCSort)dimensionSort).getValue().getTargetFieldId();
                return new DescSort(index, new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)));
            }
            default:
                return new AscSort(index);
        }
    }

    public static Sort adaptorDetailDimensionSort(FineDimensionSort dimensionSort, int index) {
        if (dimensionSort == null) {
            return new NoneSort();
        }
        return adaptorDimensionSort(dimensionSort, index);
    }

}
