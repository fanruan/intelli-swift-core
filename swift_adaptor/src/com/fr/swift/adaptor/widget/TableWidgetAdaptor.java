package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.provider.SwiftTableConfProvider;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.utils.FineFieldUtils;
import com.fr.swift.adaptor.struct.node.BIGroupNodeFactory;
import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupFormulaTarget;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/21
 * 分组表
 */
public class TableWidgetAdaptor {
    public static BIGroupNode calculate(TableWidget widget) {
        BIGroupNode resultNode = null;
        SwiftResultSet resultSet;
        try {
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget));
            resultNode = BIGroupNodeFactory.create((GroupByResultSet) resultSet);
        } catch (Exception e) {

        }
        return resultNode;
    }

    static QueryInfo buildQueryInfo(TableWidget widget) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());

        List<Dimension> dimensions = getDimensions(widget);
        List<Metric> metrics = getMetrics(widget);

        GroupTarget[] targets = getTargets(widget);
        Expander expander = null;
        FineBusinessTable table = new SwiftTableConfProvider().getSingleTable(widget.getTableName());
        SourceKey sourceKey = IndexingDataSourceFactory.transformDataSource(table).getSourceKey();
        TableGroupQueryInfo tableGroupQueryInfo = new TableGroupQueryInfo(
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                sourceKey
        );
        return new GroupQueryInfo(cursor, queryId, filterInfo,
                new TableGroupQueryInfo[]{tableGroupQueryInfo},
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }

    private static List<Metric> getMetrics(TableWidget widget) throws Exception {
        List<Metric> metrics = new ArrayList<Metric>();
        List<FineTarget> targets = widget.getTargetList();
        for (int i = 0; i < targets.size(); i++) {
            metrics.add(toMetric(targets.get(i), i));
        }
        return metrics;
    }

    private static List<Dimension> getDimensions(TableWidget widget) throws Exception {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        List<FineDimension> fineDims = widget.getDimensionList();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(fineDim, i));
        }
        return dimensions;
    }

    private static GroupTarget[] getTargets(TableWidget widget) throws Exception {
        List<FineTarget> fineTargets = widget.getTargetList();
        GroupTarget[] targets = new GroupTarget[fineTargets.size()];
        for (int i = 0, size = fineTargets.size(); i < size; i++) {
            targets[i] = new GroupFormulaTarget(i);
        }
        return targets;
    }

    private static boolean isMetric(FineDimension dimension) throws FineEngineException {
        int type = FineFieldUtils.getField(dimension.getFieldId()).getType();
        ColumnType columnType = ColumnTypeAdaptor.adaptColumnType(type);
        return columnType == ColumnType.NUMBER;
    }

    private static Dimension toDimension(FineDimension fineDim, int index) throws Exception {
        SourceKey key = new SourceKey(fineDim.getId());
        // fixme 传text可能有问题
        ColumnKey colKey = new ColumnKey(fineDim.getText());

        Group group = GroupAdaptor.adaptGroup(fineDim.getGroup());

        FilterInfo filterInfo = null;

        return new GroupDimension(index, key, colKey, group, fineDim.getSort() == null ? new AscSort(index) : adaptSort(fineDim.getSort(), index), filterInfo);
    }


    private static Metric toMetric(FineTarget target, int index) throws Exception {
        SourceKey key = new SourceKey(target.getId());
        // fixme 传text可能有问题
        ColumnKey colKey = new ColumnKey(target.getText());

        FilterInfo filterInfo = null;
        // TODO: 2018/3/21   先跑通流程吧。。
        Aggregator agg = new SumAggregate();

        return new GroupMetric(index, key, colKey, filterInfo, agg);
    }

    private static Sort adaptSort(FineDimensionSort sort, int index) {
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