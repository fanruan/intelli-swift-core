package com.fr.swift.cal.builder;

import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/18
 */
public abstract class AbstractLocalGroupQueryBuilder implements LocalGroupQueryBuilder {
    protected List<Column> getDimensionSegments(Segment segment, Dimension[] dimensions) {
        List<Column> dimensionColumns = new ArrayList<Column>();
        for (Dimension dimension : dimensions) {
            List<Column> columnList = new ArrayList<Column>();
            Column column = segment.getColumn(dimension.getColumnKey());
            columnList.add(column);
            Group group = dimension.getGroup();
            GroupOperator operator = null;
            String fieldId = dimension.getSort().getTargetFieldId();
            if(fieldId != null) {
                columnList.add(segment.getColumn(new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId))));
            }
            if (group != null) {
                operator = group.getGroupOperator();
            }
            if (operator != null) {
                column = operator.group(columnList);
            }
            dimensionColumns.add(column);
        }
        return dimensionColumns;
    }

    protected List<Column> getMetricSegments(Segment segment, List<Metric> metrics) {
        List<Column> metricColumns = new ArrayList<Column>();
        for (Metric metric : metrics) {
            Column column = segment.getColumn(metric.getColumnKey());
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
