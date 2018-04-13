package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeOptionsBean;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeListWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.control.tree.BITreeItem;
import com.finebi.conf.structure.result.control.tree.BITreeListResult;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/3/27.
 */
public class TreeListWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeListWidgetAdaptor.class);

    public static BITreeListResult calculate(TreeListWidget widget) {
        try {
            // TODO: 2018/4/13 勾选节点是否应该刷新树控件呢（感觉不需要）？如果要刷新的话，这边如何判断更新那一层节点呢？
            // BITreeResult是一个列表结构，通过parentValues构造对当前floor分组值的过滤
            TreeOptionsBean bean = widget.getValue().getOptions().getTreeOptions();
            List<String> parents = bean.getParentValues();
            parents = parents == null ? new ArrayList<String>() : parents;
            // 根据有多少个父节点来判断要加载哪个维度的子节点
            FineDimension dimension = widget.getDimensionList().get(parents.size());
            FilterInfo filterInfo = TreeWidgetAdaptor.parents2FilterInfo(widget.getFilters(), parents,
                    widget.getDimensionList());
            List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            // 当前TreeItem是否有子节点
            boolean isParent = parents.size() < bean.getFloors() - 1;
            // TODO: 2018/4/13 暂时不分页
            return new TreeListResult(false,
                    TreeWidgetAdaptor.createTreeItems(isParent, values, widget.getSelectedValues()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
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
