package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeOptionsBean;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.control.tree.BITreeItem;
import com.finebi.conf.structure.result.control.tree.BITreeResult;
import com.fr.stable.StringUtils;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/3/26.
 */
public class TreeWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeWidgetAdaptor.class);

    public static BITreeResult calculate(TreeWidget treeWidget) {
        try {
            TreeOptionsBean bean = treeWidget.getValue().getOptions().getTreeOptions();
            List<String> parents = bean.getParentValues();
            parents = parents == null ? new ArrayList<String>() : parents;
            // 根据有多少个父节点来判断要加载哪个维度的子节点
            FineDimension dimension = treeWidget.getDimensionList().get(parents.size());
            FilterInfo filterInfo = parents2FilterInfo(treeWidget.getFilters(), parents, treeWidget.getDimensionList());
            List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, treeWidget.getWidgetId());
            boolean isParent = parents.size() < bean.getFloors() - 1;
            // TODO: 2018/4/13 BITreeResult暂时不分页
            return new TreeResult(false, createTreeItems(isParent, values, treeWidget.getSelectedValues()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static FilterInfo parents2FilterInfo(List<FineFilter> fineFilters, List<String> parents, List<FineDimension> dimensions) {
        List<FilterInfo> infoList = new ArrayList<FilterInfo>();
        infoList.add(FilterInfoFactory.transformFineFilter(fineFilters));
        for (int i = 0; i < parents.size(); i++) {
            FineDimension dimension = dimensions.get(i);
            String fieldName = SwiftEncryption.decryptFieldId(dimension.getFieldId())[1];
            final String data = parents.get(i);
            infoList.add(new SwiftDetailFilterInfo<Set<String>>(fieldName,
                    new HashSet<String>() {{ add(data); }}, SwiftDetailFilterType.STRING_IN));
        }
        return new GeneralFilterInfo(infoList, GeneralFilterInfo.AND);
    }

    static List<BITreeItem> createTreeItems(boolean isParent, List values, Map selectedValues) {
        List<BITreeItem> items = new ArrayList<BITreeItem>();
        for (Object value : values) {
            BITreeItem item = new BITreeItem();
            item.setId("1");
            item.setParent(isParent);
            item.setValue(value == null ? StringUtils.EMPTY : value.toString());
            item.setText(value == null ? StringUtils.EMPTY : value.toString());
            item.setTitle(value == null ? StringUtils.EMPTY : value.toString());
            item.setTimes(1);
            items.add(item);
            // TODO: 2018/4/13  
            // halfChecked表示该节点有部分子节点被勾选。
            // 如果子节点全部被勾选，那么应该是checked=true，但是该如何判断自己点被全部勾选了呢？
            // selectedValues是一个嵌套的map，可以知道当前层当前节点的子节点被选中的个数，但是并不能确定子节点总共有多少个。
            // 如果当前节点的selectedValues.size() == 0，那么halfChecked = false && checked = false
            // half这个属性至今是个谜，另外看4.1的代码，这个id也要拼成类型"1_2_1"这样的结构也不清楚
            boolean checked = selectedValues.containsKey(value);
            item.setChecked(checked);
        }
        return items;
    }

    private static class TreeResult implements BITreeResult {
        // 分页
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
