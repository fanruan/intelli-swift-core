package com.fr.swift.adaptor.transformer.filter.date;

import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeOffset;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;

/**
 * Created by Lyon on 2018/5/9.
 */
public class DateRangeOffsetAdaptor {

    public static SwiftDateInRangeFilterValue create(DateRangeOffset offset, SwiftDateInRangeFilterValue value) {
        value.setStart(DateUtils.dateOffset2Range(value.getStart(), offset)[0]);
        value.setEnd(DateUtils.dateOffset2Range(value.getEnd(), offset)[1]);
        return value;
    }
}
