package com.fr.swift.adaptor.transformer.filter.date;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeOffset;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateWidgetBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateWidgetBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateWidgetInterval;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateWidgetPanel;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;

/**
 * Created by Lyon on 2018/5/9.
 */
public class DateWidgetBeanAdaptor {

    private static final int START_BEAN_OF_INTERVAL = 1;
    private static final int END_BEAN_OF_INTERVAL = 2;

    public static SwiftDateInRangeFilterValue create(DateWidgetBean bean) {
        if (bean == null) {
            return new SwiftDateInRangeFilterValue();
        }
        DateWidgetBeanValue value = bean.getWidget();
        if (value == null) {
            return new SwiftDateInRangeFilterValue();
        }
        SwiftDateInRangeFilterValue filterValue = null;
        DateRangeOffset offset = bean.getOffset();
        int type = value.getPoint();
        switch (type) {
            case BICommonConstants.DATE_TIME_TYPE.INTERVAL: {
                DateRangeValueBean dateRangeValueBean = ((DateWidgetInterval) value).getValue();
                if (bean.getStartOrEnd() == START_BEAN_OF_INTERVAL) {
                    filterValue = convertDateFilterBean(dateRangeValueBean.getStart(), offset);
                    break;
                }
                if (bean.getStartOrEnd() == END_BEAN_OF_INTERVAL) {
                    filterValue = convertDateFilterBean(dateRangeValueBean.getEnd(), offset);
                    break;
                }
                filterValue = DateRangeValueBeanAdaptor.create(dateRangeValueBean);
                if (offset != null) {
                    // 这边只能是同一步长移动？
                    filterValue.setStart(DateUtils.dateOffset2long(filterValue.getStart(), offset));
                    filterValue.setEnd(DateUtils.dateOffset2long(filterValue.getEnd(), offset));
                }
                break;
            }
            case BICommonConstants.DATE_TIME_TYPE.POINT: {
                DateFilterBean dateFilterBean = ((DateWidgetPanel) value).getValue();
                filterValue = convertDateFilterBean(dateFilterBean, offset);
                break;
            }
        }
        return filterValue;
    }

    private static SwiftDateInRangeFilterValue convertDateFilterBean(DateFilterBean bean, DateRangeOffset offset) {
        SwiftDateInRangeFilterValue filterValue = DateUtils.create(bean);
        if (offset != null) {
            filterValue = DateRangeOffsetAdaptor.create(offset, filterValue);
        }
        return filterValue;
    }

    public static SwiftDateInRangeFilterValue createDateLessThanFilterInfo(DateWidgetBean bean) {
        if (bean == null) {
            return new SwiftDateInRangeFilterValue();
        }
        // 在某一时刻之前。-1ms，因为DateInRangeFilter的范围是左右包含的(startTimeIncluded, endTimeIncluded)
        SwiftDateInRangeFilterValue range = create(bean);
        long value = -1;
        if (bean.getOffset() != null) {
            int position = bean.getOffset().getPosition();
            if (position == DateUtils.POSITION_END) {
                // 比如某个日期之前-》过滤控件值-》年份控件-》一年前的年末
                value = range.getEnd() - 1;
            }
        }
        if (value == -1) {
            value = range.getStart() - 1;
        }
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        filterValue.setEnd(value);
        return filterValue;
    }

    public static SwiftDateInRangeFilterValue createDateMoreThanFilterInfo(DateWidgetBean bean) {
        // 在某一时刻之后。+1ms，因为DateInRangeFilter的范围是左右包含的(startTimeIncluded, endTimeIncluded)
        SwiftDateInRangeFilterValue range = create(bean);
        long value = -1;
        if (bean.getOffset() != null) {
            int position = bean.getOffset().getPosition();
            if (position == DateUtils.POSITION_START) {
                // 比如某个日期之后-》过滤控件值-》年份控件-》一年前的年初
                value = range.getStart() + 1;
            }
        }
        if (value == -1) {
            value = range.getEnd() + 1;
        }
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        filterValue.setStart(value);
        return filterValue;
    }
}
