package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 下拉文本
 */
public class DropdownStringWidgetAdaptor {
    static QueryInfo buildQueryInfo(StringControlWidget widget) {
        List<String> selectedValues = widget.getSelectedValues();
        List<String> queryWords = widget.getKeywords();
        int clickMore = widget.getTimes();
        FilterInfo filterInfo = null;

        return null/*new GroupQueryInfo(null, widget.getWidgetId(), filterInfo)*/;
    }
}