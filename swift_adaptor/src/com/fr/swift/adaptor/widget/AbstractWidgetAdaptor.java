package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.SourceKey;
import com.fr.swift.utils.BusinessTableUtils;

/**
 * Created by pony on 2018/4/20.
 */
public abstract class AbstractWidgetAdaptor {

    protected static SourceKey getSourceKey(String fieldId){
        return new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
    }

    protected static String getColumnName(String fieldId){
        return BusinessTableUtils.getFieldNameByFieldId(fieldId);
    }

    protected static Sort adaptSort(FineDimensionSort sort, int index) {
        switch (sort.getType()) {
            case BIReportConstant.SORT.ASC:
                return new AscSort(index);
            case BIReportConstant.SORT.DESC:
                return new DescSort(index);
            case BIReportConstant.SORT.NONE:
                return new NoneSort();
            default:
                return null;
        }
    }
}
