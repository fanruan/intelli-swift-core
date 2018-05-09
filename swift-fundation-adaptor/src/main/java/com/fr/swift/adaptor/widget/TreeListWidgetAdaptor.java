package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeOptionsBean;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
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

    public static BITreeListResult calculate(TreeListWidget treeWidget) {
        BITreeListResult result = null;
        try {
            TreeOptionsBean bean = treeWidget.getValue().getOptions().getTreeOptions();
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(null, treeWidget.getFilters());
            // TODO: 2018/4/13 BITreeResult暂时不分页
            List<BITreeItem> treeItems = TreeWidgetAdaptor.createTreeItemList(treeWidget, bean,
                    filterInfo, treeWidget.getDimensionList());
            result = new TreeListResult(false, treeItems);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return result;
    }

    private static class TreeListResult implements BITreeListResult {
        // 分页
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
