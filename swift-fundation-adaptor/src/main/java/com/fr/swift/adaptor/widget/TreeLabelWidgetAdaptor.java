package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeLabelWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.tree.BITreeLabelResult;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lyon
 * @date 2018/3/27
 */
public class TreeLabelWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeLabelWidgetAdaptor.class);

    /**
     * 功能传过来的神秘代码
     */
    private static final String EMPTY_SELECTED_VALUE = "_*_";

    public static BITreeLabelResult calculate(TreeLabelWidget labelWidget) {
        try {
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(StringUtils.EMPTY, labelWidget.getFilters());
            List<List<String>> items = new ArrayList<List<String>>();
            List<FineDimension> fineDimensions = labelWidget.getDimensionList();
            List<List<String>> selectedValues = labelWidget.getSelectedValues();
            for (int i = 0; i < fineDimensions.size(); i++) {
                // 树标签要根据上一层维度选择的值进行过滤
                if (i > 0 && !selectedValues.get(i - 1).isEmpty() && !StringUtils.equals(selectedValues.get(i - 1).get(0), EMPTY_SELECTED_VALUE)) {
                    filterInfo = selectedValues2FilterInfo(BusinessTableUtils.getFieldNameByFieldId(fineDimensions.get(i - 1).getFieldId()),
                            selectedValues.get(i - 1), filterInfo);
                }
                List values = QueryUtils.getOneDimensionFilterValues(fineDimensions.get(i),
                        filterInfo, labelWidget.getWidgetId());
                items.add(toStringList(values));
            }
            return new TreeLabelResult(items, labelWidget.getValue().getOptions().getTreeOptions().getSelectedValues());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private static FilterInfo selectedValues2FilterInfo(String fieldName, List<String> selectedValues, FilterInfo filterInfo) {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        filterInfoList.add(filterInfo);
        filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(fieldName), new HashSet<String>(selectedValues),
                SwiftDetailFilterType.STRING_IN));
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
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
