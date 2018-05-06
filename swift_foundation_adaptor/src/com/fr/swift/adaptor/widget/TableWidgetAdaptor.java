package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.expander.ExpanderBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.dimension.FineDimensionImpl;
import com.finebi.conf.internalimp.dashboard.widget.dimension.sort.DimensionTargetSort;
import com.finebi.conf.internalimp.dashboard.widget.filter.CustomLinkConfItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionDrill;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.adaptor.struct.node.BIGroupNodeAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.SortAdaptor;
import com.fr.swift.adaptor.widget.expander.ExpanderFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.DimensionInfo;
import com.fr.swift.query.adapter.dimension.DimensionInfoImpl;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.adapter.target.cal.TargetInfoImpl;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
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
        FilterInfo filterInfo = getFilterInfo(widget, dimensions);
        List<ExpanderBean> rowExpand = widget.getValue().getRowExpand();
        Expander expander = ExpanderFactory.create(widget.isOpenRowNode(), dimensions.size(),
                rowExpand == null ? new ArrayList<ExpanderBean>() : rowExpand);
        DimensionInfo dimensionInfo = new DimensionInfoImpl(cursor, filterInfo, expander, dimensions.toArray(new Dimension[dimensions.size()]));
        return new GroupQueryInfo(queryId, sourceKey, dimensionInfo, targetInfo);
    }

    static FilterInfo getFilterInfo(AbstractTableWidget widget, List<Dimension> dimensions) throws Exception {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        dealWithWidgetFilter(filterInfoList, widget);
        dealWithLink(filterInfoList, widget);
        dealWithDrill(filterInfoList, widget);
        dealWithDimensionDirectFilter(filterInfoList, dimensions);
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    private static void dealWithDimensionDirectFilter(List<FilterInfo> filterInfoList, List<Dimension> dimensions) {
        // 维度上的直接过滤，提取出来
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && !filter.isMatchFilter()) {
                filterInfoList.add(dimension.getFilter());
            }
        }
    }

    private static void dealWithDrill(List<FilterInfo> filterInfoList, AbstractTableWidget widget) throws Exception {
        for (FineDimension fineDimension : widget.getDimensionList()) {
            FineDimensionDrill drill = fineDimension.getDimensionDrill();
            if (drill != null) {
                String columnName = getColumnName(drill.getFromDimension().getFieldId());
                String value = drill.getFromValue();
//                Set<String> values = new HashSet<String>();
                filterInfoList.add(LinkageAdaptor.dealFilterInfo(new ColumnKey(columnName), value, ((FineDimensionImpl) fineDimension).getValue()));
//                values.add(value);
//                filterInfoList.add(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(columnName), values, SwiftDetailFilterType.STRING_IN));
            }
        }
        widget.getValue().getDrillList();
    }

    private static void dealWithLink(List<FilterInfo> filterInfoList, AbstractTableWidget widget) throws SQLException {
        //联动设置
        Map<String, WidgetLinkItem> linkItemMap = widget.getValue().getLinkage();
        //手动联动配置
        Map<String, List<CustomLinkConfItem>> customLinkConf = widget.getValue().getCustomLinkConf();
        if (linkItemMap != null) {
            for (Map.Entry<String, WidgetLinkItem> entry : linkItemMap.entrySet()) {
                WidgetLinkItem widgetLinkItem = entry.getValue();
                String id = entry.getKey();
                if (customLinkConf != null && customLinkConf.containsKey(id)) {
                    dealWithCustomLink(widget.getTableName(), filterInfoList, widgetLinkItem, customLinkConf.get(id));
                } else {
                    dealWithAutoLink(widget.getTableName(), filterInfoList, widgetLinkItem);
                }
            }
        }
    }

    private static void dealWithAutoLink(String tableName, List<FilterInfo> filterInfoList, WidgetLinkItem widgetLinkItem) {
        //根据点击的值，创建过滤条件
        LinkageAdaptor.handleClickItem(tableName, widgetLinkItem, filterInfoList);
    }

    /**
     * 根据点击的条件发一个明细的query，查询下对应的值
     *
     * @param filterInfoList
     * @param widgetLinkItem
     * @param customLinkConfItems
     */
    private static void dealWithCustomLink(String tableName, List<FilterInfo> filterInfoList, WidgetLinkItem widgetLinkItem, List<CustomLinkConfItem> customLinkConfItems) throws SQLException {
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
        TableWidgetBean fromWidget = LinkageAdaptor.handleClickItem(tableName, widgetLinkItem, filterInfos);
        //分组表查询
        FilterInfo filterInfo = new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND);
        GroupQueryInfo queryInfo = new GroupQueryInfo(fromWidget.getwId(), fromColumns[0].getSourceKey(),
                new DimensionInfoImpl(new AllCursor(), filterInfo, null, fromColumns),
                new TargetInfoImpl(0, new ArrayList<Metric>(0), new ArrayList<GroupTarget>(0), new ArrayList<ResultTarget>(0), new ArrayList<Aggregator>(0)));
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        Set[] results = new HashSet[toColumns.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = new HashSet();
        }
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            for (int i = 0; i < row.getSize(); i++) {
                results[i].add(row.getValue(i));
            }
        }
        for (int i = 0; i < toColumns.length; i++) {
            filterInfoList.add(new SwiftDetailFilterInfo(new ColumnKey(toColumns[i]), results[i], SwiftDetailFilterType.STRING_IN));
        }
    }

    private static void dealWithWidgetFilter(List<FilterInfo> filterInfoList, AbstractTableWidget widget) throws Exception {
        List<FineFilter> filters = dealWithTargetFilter(widget, widget.getFilters());
        if (filters != null && !filters.isEmpty()) {
            filterInfoList.add(FilterInfoFactory.transformFineFilter(widget.getTableName(), filters));
        }
    }

    static List<Dimension> getDimensions(SourceKey sourceKey, List<FineDimension> fineDims, List<FineTarget> targets) throws SQLException {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(sourceKey, fineDim, i, size, targets));
        }
        return dimensions;
    }

    private static Dimension toDimension(SourceKey sourceKey, FineDimension fineDim, int index, int size, List<FineTarget> targets) throws SQLException {
        String columnName = getColumnName(fineDim);
        String tableName = getTableName(fineDim.getFieldId());
        ColumnKey colKey = new ColumnKey(columnName);

        Group group = GroupAdaptor.adaptDashboardGroup(fineDim);

        FilterInfo filterInfo = FilterInfoFactory.transformDimensionFineFilter(tableName, fineDim, index == size - 1, targets);

        return new GroupDimension(index, sourceKey, colKey, group, SortAdaptor.adaptorDimensionSort(fineDim.getSort(), getSortIndex(fineDim.getSort(), index, targets, size)),
                filterInfo);
    }

    private static int getSortIndex(FineDimensionSort sort, int index, List<FineTarget> targets, int size){
        if (sort instanceof DimensionTargetSort){
            String targetId = ((DimensionTargetSort) sort).getTargetId();
            for (int i = 0; i < targets.size(); i++){
                if (ComparatorUtils.equals(targets.get(i).getId(), targetId)){
                    return i + size;
                }
            }
        }
        return index;
    }
}