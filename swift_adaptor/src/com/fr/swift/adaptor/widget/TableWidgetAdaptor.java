package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.utils.FineFieldUtils;
import com.fr.swift.adaptor.transformer.ColumnTypeAdaptor;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.GroupMetric;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupFormulaTarget;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/21
 */
public class TableWidgetAdaptor {
    public static BIGroupNode calculate(TableWidget widget) {
        return null;
    }

    static QueryInfo buildQueryInfo(TableWidget widget) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        FilterInfo filterInfo = null;

        List<Dimension> dimensions = new ArrayList<Dimension>();
        List<Metric> metrics = new ArrayList<Metric>();
        getDimensions(widget, dimensions, metrics);

        GroupTarget[] targets = getTargets(widget);
        Expander expander = null;

        return new GroupQueryInfo(cursor, queryId, filterInfo, null,
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }

    private static void getDimensions(TableWidget widget, List<Dimension> dimensions, List<Metric> metrics) throws Exception {
        List<FineDimension> fineDims = widget.getDimensionList();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            if (isMetric(fineDim)) {
                metrics.add(toMetric(fineDim, i));
            } else {
                dimensions.add(toDimension(fineDim, i));
            }
        }
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

    private static Dimension toDimension(FineDimension fineDim, int index) {
        SourceKey key = new SourceKey(fineDim.getId());
        ColumnKey colKey = new ColumnKey(fineDim.getFieldId());

        Group group = GroupAdaptor.adaptGroup(fineDim.getGroup());

        FilterInfo filterInfo = null;

        return new GroupDimension(index, key, colKey, group, adaptSort(fineDim.getSort(), index), filterInfo);
    }


    private static Metric toMetric(FineDimension fineDim, int index) {
        SourceKey key = new SourceKey(fineDim.getId());
        ColumnKey colKey = new ColumnKey(fineDim.getFieldId());

        FilterInfo filterInfo = null;

        Aggregator agg = null;

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