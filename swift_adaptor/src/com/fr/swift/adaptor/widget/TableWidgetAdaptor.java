package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.CustomLinkConfItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionDrill;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.adaptor.struct.node.BIGroupNodeAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.expander.ExpanderFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.AllCursor;
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
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
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
public class TableWidgetAdaptor extends AbstractTableWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableWidgetAdaptor.class);

    public static BITableResult calculate(TableWidget widget) {
        BIGroupNode resultNode;
        SwiftResultSet resultSet;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget, targetInfo));
            GroupNode groupNode = (GroupNode) ((NodeResultSet) resultSet).getNode();
            // 取出实际查询的指标
            groupNode = TargetCalculatorUtils.getShowTargetsForGroupNode(groupNode, targetInfo.getTargetsForShowList());
            resultNode = new BIGroupNodeAdaptor(groupNode);
        } catch (Exception e) {
            resultNode = new BIGroupNodeAdaptor(new GroupNode(-1, null));
            LOGGER.error(e);
        }
        return new TableResult(resultNode, false, false);
    }

    static class TableResult implements BITableResult {
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

    private static QueryInfo buildQueryInfo(TableWidget widget, TargetInfo targetInfo) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        SourceKey sourceKey = getSourceKey(widget);
        List<Dimension> dimensions = getDimensions(sourceKey, widget.getDimensionList(), widget.getTargetList());
        FilterInfo filterInfo = getFilterInfo(widget);

        GroupTarget[] targets = targetInfo.getGroupTargets().toArray(new GroupTarget[targetInfo.getGroupTargets().size()]);
        List<Metric> metrics = targetInfo.getMetrics();
        Expander expander = ExpanderFactory.create(widget.isOpenRowNode(), dimensions.size(),
                widget.getValue().getRowExpand());
        return new GroupQueryInfo(cursor, queryId, sourceKey, filterInfo,
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander, targetInfo.getTargetLength());
    }

    private static FilterInfo getFilterInfo(TableWidget widget) throws Exception {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        dealWithWidgetFilter(filterInfoList, widget);
        dealWithLink(filterInfoList, widget);
        dealWithDrill(filterInfoList, widget);
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    private static void dealWithDrill(List<FilterInfo> filterInfoList, TableWidget widget) throws Exception {
        for (FineDimension fineDimension : widget.getDimensionList()) {
            FineDimensionDrill drill = fineDimension.getDimensionDrill();
            if (drill != null) {
                String columnName = getColumnName(drill.getFromDimension().getFieldId());
                String value = drill.getFromValue();
                Set<String> values = new HashSet<String>();
                values.add(value);
                filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(columnName, values, SwiftDetailFilterType.STRING_IN));
            }
        }
        widget.getValue().getDrillList();
    }

    private static void dealWithLink(List<FilterInfo> filterInfoList, TableWidget widget) throws SQLException {
        //联动设置
        Map<String, WidgetLinkItem> linkItemMap = widget.getValue().getLinkage();
        //手动联动配置
        Map<String, List<CustomLinkConfItem>> customLinkConf = widget.getValue().getCustomLinkConf();
        if (linkItemMap != null) {
            for (Map.Entry<String, WidgetLinkItem> entry : linkItemMap.entrySet()) {
                WidgetLinkItem widgetLinkItem = entry.getValue();
                String id = entry.getKey();
                if (customLinkConf != null && customLinkConf.containsKey(id)) {
                    dealWithCustomLink(filterInfoList, widgetLinkItem, customLinkConf.get(id));
                } else {
                    dealWithAutoLink(filterInfoList, widgetLinkItem);
                }
            }
        }
    }

    private static void dealWithAutoLink(List<FilterInfo> filterInfoList, WidgetLinkItem widgetLinkItem) {
        //根据点击的值，创建过滤条件
        ClickValue clickValue = widgetLinkItem.getClicked();
        List<ClickValueItem> clickedList = clickValue.getValue();
        TableWidgetBean fromWidget = (TableWidgetBean) widgetLinkItem.getWidget();
        for (ClickValueItem clickValueItem : clickedList) {
            String value = clickValueItem.getText();
            Set<String> values = new HashSet<String>();
            values.add(value);
            WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
            filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(getColumnName(bean.getFieldId()), values, SwiftDetailFilterType.STRING_IN));
        }
    }

    /**
     * 根据点击的条件发一个明细的query，查询下对应的值
     *
     * @param filterInfoList
     * @param widgetLinkItem
     * @param customLinkConfItems
     */
    private static void dealWithCustomLink(List<FilterInfo> filterInfoList, WidgetLinkItem widgetLinkItem, List<CustomLinkConfItem> customLinkConfItems) throws SQLException {
        //自定义设置的维度
        Dimension[] fromColumns = new Dimension[customLinkConfItems.size()];
        //要过滤的维度
        String[] toColumns = new String[customLinkConfItems.size()];
        for (int i = 0; i < customLinkConfItems.size(); i++) {
            CustomLinkConfItem confItem = customLinkConfItems.get(i);
            fromColumns[i] = (new GroupDimension(i, getSourceKey(confItem.getFrom()), new ColumnKey(getColumnName(confItem.getFrom())), null, null, null));
            toColumns[i] = getColumnName(confItem.getTo());
        }
        //根据点击的值，创建过滤条件
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        ClickValue clickValue = widgetLinkItem.getClicked();
        List<ClickValueItem> clickedList = clickValue.getValue();
        TableWidgetBean fromWidget = (TableWidgetBean) widgetLinkItem.getWidget();
        for (ClickValueItem clickValueItem : clickedList) {
            String value = clickValueItem.getText();
            Set<String> values = new HashSet<String>();
            values.add(value);
            WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
            filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(getColumnName(bean.getFieldId()), values, SwiftDetailFilterType.STRING_IN));
        }
        //分组表查询
        GroupQueryInfo queryInfo = new GroupQueryInfo(new AllCursor(), fromWidget.getwId(), fromColumns[0].getSourceKey(),
                new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND),fromColumns, new Metric[0], null, null, 0);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        Set[] results = new HashSet[toColumns.length];
        for (int i = 0; i < results.length; i++){
            results[i] = new HashSet();
        }
        while (resultSet.next()){
            Row row = resultSet.getRowData();
            for (int i = 0; i < row.getSize(); i++){
                results[i].add(row.getValue(i));
            }
        }
        for (int i = 0; i < toColumns.length; i++){
            filterInfoList.add(new SwiftDetailFilterInfo(toColumns[i], results[i], SwiftDetailFilterType.STRING_IN));
        }
    }

    private static void dealWithWidgetFilter(List<FilterInfo> filterInfoList, TableWidget widget) throws Exception {
        List<FineFilter> filters = widget.getFilters();
        if (filters != null && !filters.isEmpty()) {
            filterInfoList.add(FilterInfoFactory.transformFineFilter(filters));
        }
    }

    static List<Dimension> getDimensions(SourceKey sourceKey, List<FineDimension> fineDims, List<FineTarget> targets) throws Exception {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(sourceKey, fineDim, i, targets));
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

    static Dimension toDimension(SourceKey sourceKey, FineDimension fineDim, int index, List<FineTarget> targets) {
        String columnName = getColumnName(fineDim.getFieldId());
        ColumnKey colKey = new ColumnKey(columnName);

        Group group = GroupAdaptor.adaptDashboardGroup(fineDim.getGroup());

        FilterInfo filterInfo = FilterInfoFactory.transformDimensionFineFilter(fineDim.getFilters(), fineDim.getId(), targets);

        return new GroupDimension(index, sourceKey, colKey, group,
                fineDim.getSort() == null ? new AscSort(index) : adaptSort(fineDim.getSort(), index), filterInfo);
    }
}