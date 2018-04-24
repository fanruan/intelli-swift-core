package com.fr.swift.adaptor.widget;

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

<<<<<<< HEAD
    static Sort adaptSort(FineDimensionSort sort, int index) {
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
=======
>>>>>>> 2f11730561d1f332415cfaf91e35670202072413
}
