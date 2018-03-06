package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.StringControlQueryInfo;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 */
public class StringControlWidgetAdaptor {
    public static BIStringDetailResult calculate(StringControlWidget widget) {
        return null;
    }

    static QueryInfo buildQueryInfo(StringControlWidget widget) {
        List<String> selectedValues = widget.getSelectedValues();
        List<String> queryWords = widget.getKeywords();
        int clickMore = widget.getTimes();
        FilterInfo filterInfo = null;

        return new StringControlQueryInfo(null, widget.getWidgetId(), filterInfo);
    }
}