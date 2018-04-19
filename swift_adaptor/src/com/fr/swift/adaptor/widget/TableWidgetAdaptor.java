package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.CustomLinkConfItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionDrill;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.adaptor.struct.node.BIGroupNodeAdaptor;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.TargetInfo;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pony
 * @date 2017/12/21
 * 分组表
 */
public class TableWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableWidgetAdaptor.class);

    public static BITableResult calculate(TableWidget widget) {
        BIGroupNode resultNode;
        SwiftResultSet resultSet;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget, targetInfo.getMetrics()));
            GroupNode groupNode = (GroupNode) ((NodeResultSet) resultSet).getNode();
            TargetCalculatorUtils.calculate(groupNode, targetInfo.getTargetCalculatorInfoList(), targetInfo.getTargetsForShowList());
            resultNode = new BIGroupNodeAdaptor(groupNode);
        } catch (Exception e) {
            resultNode = new BIGroupNodeAdaptor(new GroupNode(-1, null));
            LOGGER.error(e);
        }
        return new TableResult(resultNode, false, false);
    }

    static class TableResult implements BITableResult{
        private BIGroupNode node;
        private boolean hasNextPage;
        private boolean hasPreviousPage;

        public TableResult(BIGroupNode node, boolean hasNextPage, boolean hasPreviousPage) {
            this.node = node;
            this.hasNextPage = hasNextPage;
            this.hasPreviousPage = hasPreviousPage;
        }

        @Override
        public BIGroupNode getNode() {
            return node;
        }

        @Override
        public boolean hasNextPage() {
            return hasNextPage;
        }

        @Override
        public boolean hasPreviousPage() {
            return hasPreviousPage;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.BIGROUP;
        }
    }

    static QueryInfo buildQueryInfo(TableWidget widget, List<Metric> metrics) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();

        List<Dimension> dimensions = getDimensions(widget.getDimensionList(), widget.getTargetList());
        FilterInfo filterInfo = getFilterInfo(widget);

        GroupTarget[] targets = getTargets(widget);
        Expander expander = null;
        String fieldId = widget.getDimensionList().isEmpty() ? null : widget.getDimensionList().get(0).getFieldId();
        fieldId = fieldId != null ? fieldId : metrics.isEmpty() ? null : metrics.get(0).getSourceKey().getId();
        fieldId = fieldId == null ?
                widget.getTargetList().isEmpty() ? null : widget.getTargetList().get(0).getFieldId()
                : fieldId;
        FineBusinessTable fineBusinessTable = BusinessTableUtils.getTableByFieldId(fieldId);
        DataSource baseDataSource = DataSourceFactory.transformDataSource(fineBusinessTable);
        return new GroupQueryInfo(cursor, queryId, baseDataSource.getSourceKey(), filterInfo,
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }

    private static FilterInfo getFilterInfo(TableWidget widget) throws Exception {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        dealWithWidgetFilter(filterInfoList, widget);
        dealWithLink(filterInfoList, widget);
        dealWithDrill(filterInfoList, widget);
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    private static void dealWithDrill(List<FilterInfo> filterInfoList, TableWidget widget) throws Exception {
        for (FineDimension fineDimension : widget.getDimensionList()){
            FineDimensionDrill drill = fineDimension.getDimensionDrill();
            if (drill != null){
                String columnName = SwiftEncryption.decryptFieldId(drill.getFromDimension().getFieldId())[1];
                String value = drill.getFromValue();
                Set<String> values = new HashSet<String>();
                values.add(value);
                filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(columnName, values, SwiftDetailFilterType.STRING_IN));
            }
        }
        widget.getValue().getDrillList();
    }

    private static void dealWithLink(List<FilterInfo> filterInfoList, TableWidget widget) {
        //联动设置
        Map<String, WidgetLinkItem> linkItemMap = widget.getValue().getLinkage();
        //联动配置
        Map<String, List<CustomLinkConfItem>> linkConf = widget.getValue().getCustomLinkConf();
        if (linkItemMap != null) {
            for (Map.Entry<String, WidgetLinkItem> entry : linkItemMap.entrySet()) {
                WidgetLinkItem widgetLinkItem = entry.getValue();
                String id = entry.getKey();
                //根据联动设置找到联动配置，生成一个笛卡儿积的过滤条件
                List<CustomLinkConfItem> itemList = linkConf.get(id);
                for (CustomLinkConfItem confItem : itemList) {
                    String columnName = SwiftEncryption.decryptFieldId(confItem.getTo())[1];
                    ClickValue clickValue = widgetLinkItem.getClicked();
                    List<ClickValueItem> clickedList = clickValue.getValue();
                    for (ClickValueItem clickValueItem : clickedList) {
                        String value = clickValueItem.getText();
                        Set<String> values = new HashSet<String>();
                        values.add(value);
                        filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(columnName, values, SwiftDetailFilterType.STRING_IN));
                    }
                }
            }
        }
    }

    private static void dealWithWidgetFilter(List<FilterInfo> filterInfoList, TableWidget widget) throws Exception {
        List<FineFilter> filters = widget.getFilters();
        if (filters != null && !filters.isEmpty()){
            filterInfoList.add(FilterInfoFactory.transformFineFilter(filters));
        }
    }

    static List<Dimension> getDimensions(List<FineDimension> fineDims, List<FineTarget> targets) throws Exception {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(fineDim, i, targets));
        }
        return dimensions;
    }

    static GroupTarget[] getTargets(FineWidget widget) throws Exception {
        List<FineTarget> fineTargets = widget.getTargetList();
        fineTargets = fineTargets == null ? new ArrayList<FineTarget>() : fineTargets;
        GroupTarget[] targets = new GroupTarget[0];
        //先注释掉，加进来挂了
//        for (int i = 0, size = fineTargets.size(); i < size; i++) {
//            targets[i] = new GroupFormulaTarget(i);
//        }
        return targets;
    }

    static Dimension toDimension(FineDimension fineDim, int index, List<FineTarget> targets) {
        SourceKey key = new SourceKey(fineDim.getId());
        String columnName = SwiftEncryption.decryptFieldId(fineDim.getFieldId())[1];
        ColumnKey colKey = new ColumnKey(columnName);

        Group group = GroupAdaptor.adaptDashboardGroup(fineDim.getGroup());

        FilterInfo filterInfo = FilterInfoFactory.transformDimensionFineFilter(fineDim.getFilters(), fineDim.getId(), targets);

        return new GroupDimension(index, key, colKey, group,
                fineDim.getSort() == null ? new AscSort(index) : adaptSort(fineDim.getSort(), index), filterInfo);
    }

    static Sort adaptSort(FineDimensionSort sort, int index) {
        switch (sort.getType()) {
            case SORT.ASC:
                return new AscSort(index);
            case SORT.DESC:
                return new DescSort(index);
            case SORT.NONE:
                return new NoneSort();
            default:
                return null;
        }
    }
}