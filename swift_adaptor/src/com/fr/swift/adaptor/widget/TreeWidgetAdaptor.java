package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.tree.BITreeItem;
import com.finebi.conf.structure.result.control.tree.BITreeResult;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/3/26.
 */
public class TreeWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeWidgetAdaptor.class);

    public static BITreeResult calculate(TreeWidget treeWidget) {
        try {
            // TODO: 2018/3/27 当前功能hasNext属性设置无效。
            // BITreeResult是一个列表结构，通过parentValues构造对当前floor分组值的过滤
            // 当前功能没法点击展开操作
            FineDimension dimension = treeWidget.getDimensionList().get(0);
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(treeWidget.getFilters());
            // selectedValues是一个嵌套的map，如何确定是第几层的分组值呢？
            List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, treeWidget.getWidgetId());
            return new TreeResult(true, createTreeItems(values, treeWidget.getSelectedValues()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static List<BITreeItem> createTreeItems(List values, Map selectedValues) {
        List<BITreeItem> items = new ArrayList<BITreeItem>();
        for (Object value : values) {
            BITreeItem item = new BITreeItem();
            item.setId("1");
            item.setParent(true);
            item.setValue(value == null ? StringUtils.EMPTY : value.toString());
            item.setText(value == null ? StringUtils.EMPTY : value.toString());
            item.setTitle(value == null ? StringUtils.EMPTY : value.toString());
            item.setTimes(1);
            items.add(item);
            boolean checked = selectedValues.containsKey(value);
            item.setChecked(checked);
        }
        return items;
    }

    private static class TreeResult implements BITreeResult {

        private boolean hasNext;
        private List<BITreeItem> items;

        public TreeResult(boolean hasNext, List<BITreeItem> items) {
            this.hasNext = hasNext;
            this.items = items;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<BITreeItem> getItems() {
            return items;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.TREE;
        }
    }
}
