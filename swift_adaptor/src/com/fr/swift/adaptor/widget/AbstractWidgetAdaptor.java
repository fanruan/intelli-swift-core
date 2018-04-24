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

}
