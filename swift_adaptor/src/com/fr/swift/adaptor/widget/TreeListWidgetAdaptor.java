package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.tree.BITreeItem;
import com.finebi.conf.structure.result.control.tree.BITreeListResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.List;

/**
 * Created by Lyon on 2018/3/27.
 */
public class TreeListWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeListWidgetAdaptor.class);

    public static BITreeListResult calculate(TreeListWidget treeListWidget) {
        try {
            // TODO: 2018/3/27 当前功能hasNext属性设置无效。
            // BITreeResult是一个列表结构，通过parentValues构造对当前floor分组值的过滤
            // 当前功能没法点击展开操作
            FineDimension dimension = treeListWidget.getDimensionList().get(0);
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(treeListWidget.getFilters());
            List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, treeListWidget.getWidgetId());
            return new TreeListResult(true, TreeWidgetAdaptor.createTreeItems(values, treeListWidget.getSelectedValues()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private static class TreeListResult implements BITreeListResult {

        private boolean hasNext;
        private List<BITreeItem> items;

        public TreeListResult(boolean hasNext, List<BITreeItem> items) {
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
            return ResultType.TREE_LIST;
        }
    }
}
