package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeLabelWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.tree.BITreeLabelResult;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/3/27.
 */
public class TreeLabelWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeLabelWidgetAdaptor.class);

    public static BITreeLabelResult calculate(TreeLabelWidget labelWidget) {
        try {
            // TODO: 2018/3/27 分页怎么做？times这个属性是用来翻页的吗？有待继续猜
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(labelWidget.getFilters());
            List<List<String>> items = new ArrayList<List<String>>();
            List<FineDimension> fineDimensions = labelWidget.getDimensionList();
            for (FineDimension fineDimension : fineDimensions) {
                List values = QueryUtils.getOneDimensionFilterValues(fineDimension, filterInfo, labelWidget.getWidgetId());
                items.add(toStringList(values));
            }
            return new TreeLabelResult(items, labelWidget.getValue().getOptions().getTreeOptions().getSelectedValues());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private static List<String> toStringList(List values) {
        List<String> list = new ArrayList<String>();
        for (Object value : values) {
            list.add(value == null ? StringUtils.EMPTY : value.toString());
        }
        return list;
    }

    private static class TreeLabelResult implements BITreeLabelResult {

        private List<List<String>> items;
        private List<List<String>> values;

        public TreeLabelResult(List<List<String>> items, List<List<String>> values) {
            this.items = items;
            this.values = values;
        }

        @Override
        public List<List<String>> getItems() {
            return items;
        }

        @Override
        public List<List<String>> getValues() {
            return values;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.TREE_LABEL;
        }
    }
}
