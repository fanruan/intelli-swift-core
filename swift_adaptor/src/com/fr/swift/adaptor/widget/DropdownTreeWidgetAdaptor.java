package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 下拉树
 */
public class DropdownTreeWidgetAdaptor {
    static QueryInfo buildQueryInfo(TreeWidget widget) throws Exception {
        List<FineDimension> fineDims = widget.getDimensions();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
        return null;
    }
}