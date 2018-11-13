package com.fr.swift.adaptor.transformer.filter.date;

import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;

/**
 * Created by Lyon on 2018/5/9.
 */
public class DateRangeValueBeanAdaptor {

    public static SwiftDateInRangeFilterValue create(DateRangeValueBean bean) {
        SwiftDateInRangeFilterValue value = new SwiftDateInRangeFilterValue();
        if (bean.getStart() != null) {
            value.setStart(DateUtils.dateFilterBean2Long(bean.getStart(), true));
        }
        if (bean.getEnd() != null) {
            value.setEnd(DateUtils.dateFilterBean2Long(bean.getEnd(), false));
        }
        return value;
    }
}
