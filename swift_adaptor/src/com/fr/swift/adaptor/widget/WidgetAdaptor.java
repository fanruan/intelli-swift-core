package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIDesignConstants.DESIGN.WIDGET;
import com.finebi.conf.internalimp.dashboard.widget.control.number.SingleSliderWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.string.ListLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeLabelWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.fr.swift.cal.QueryInfo;

/**
 * @author pony
 * @date 2017/12/21
 */
public class WidgetAdaptor {
    static QueryInfo buildQueryInfo(FineWidget widget) throws Exception {
        switch (widget.getType()) {
            case WIDGET.TABLE:
                return TableWidgetAdaptor.buildQueryInfo((TableWidget) widget);
            case WIDGET.DETAIL:
                return DetailWidgetAdaptor.buildQueryInfo((DetailWidget) widget);
            case WIDGET.CROSS_TABLE:
                return CrossTableWidgetAdaptor.buildQueryInfo((CrossTableWidget) widget);
            case WIDGET.STRING:
                return DropdownStringWidgetAdaptor.buildQueryInfo((StringControlWidget) widget);
            case WIDGET.STRING_LABEL:
                return StringLabelWidgetAdaptor.buildQueryInfo((ListLabelWidget) widget);
            case WIDGET.INTERVAL_SLIDER:
                return NumberSlideWidgetAdaptor.buildQueryInfo((SingleSliderWidget) widget);
            case WIDGET.TREE:
                return DropdownTreeWidgetAdaptor.buildQueryInfo((TreeWidget) widget);
            case WIDGET.TREE_LABEL:
                return TreeLabelWidgetAdaptor.buildQueryInfo((TreeLabelWidget) widget);
            case WIDGET.TREE_LIST:
                return TreeListWidgetAdaptor.buildQueryInfo((TreeListWidget) widget);
            case WIDGET.YEAR:
            case WIDGET.QUARTER:
            case WIDGET.MONTH:
            default:
                return null;
        }
    }
}