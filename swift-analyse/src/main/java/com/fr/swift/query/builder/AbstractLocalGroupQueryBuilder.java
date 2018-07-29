package com.fr.swift.query.builder;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/18
 */
public abstract class AbstractLocalGroupQueryBuilder implements LocalGroupQueryBuilder {

    static boolean[] isGlobalIndexed(List<Dimension> dimensions) {
        boolean[] booleans = new boolean[dimensions.size()];
        for (int i = 0; i < dimensions.size(); i++) {
            booleans[i] = dimensions.get(i).getIndexInfo().isGlobalIndexed();
        }
        return booleans;
    }

    static List<Pair<Column, IndexInfo>> getDimensionSegments(Segment segment, List<Dimension> dimensions) {
        List<Pair<Column, IndexInfo>> dimensionColumns = new ArrayList<Pair<Column, IndexInfo>>();
        for (Dimension dimension : dimensions) {
            List<Column> columnList = new ArrayList<Column>();
            Column column = dimension.getColumn(segment);
            columnList.add(column);
            Group group = dimension.getGroup();
            GroupOperator operator = null;
            Sort sort = dimension.getSort();
            if(sort != null && sort.getColumnKey() != null) {
                columnList.add(segment.getColumn(sort.getColumnKey()));
            }
            if (group != null) {
                operator = group.getGroupOperator();
            }
            if (operator != null) {
                column = operator.group(columnList);
            }
            dimensionColumns.add(Pair.of(column, dimension.getIndexInfo()));
        }
        return dimensionColumns;
    }

    protected List<Column> getMetricSegments(Segment segment, List<Metric> metrics) {
        List<Column> metricColumns = new ArrayList<Column>();
        for (Metric metric : metrics) {
            Column column = metric.getColumn(segment);
            metricColumns.add(column);
        }
        return metricColumns;
    }

    protected List<Aggregator> getAggregators(List<Metric> metrics) {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics) {
            aggregators.add(metric.getAggregator());
        }
        return aggregators;
    }

    protected List<GroupTarget> getTargets(List<GroupTarget> targets) {
        List<GroupTarget> targetList = new ArrayList<GroupTarget>();
        if (targets != null) {
            for (GroupTarget target : targets) {
                targetList.add(target);
            }
        }
        return targetList;
    }
}
