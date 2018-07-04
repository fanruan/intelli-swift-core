package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NumberInRangeFilterBean extends DetailFilterInfoBean<SwiftNumberInRangeFilterValue> {

    {
        type = SwiftDetailFilterType.NUMBER_IN_RANGE;
    }

    @Override
    public SwiftNumberInRangeFilterValue getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(SwiftNumberInRangeFilterValue filterValue) {
        this.filterValue = filterValue;
    }
}
