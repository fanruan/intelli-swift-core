package com.fr.swift.adaptor.transformer.cal;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.query.aggregator.AverageAggregate;
import com.fr.swift.query.filter.detail.impl.number.NumberAverageFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.dimension.DimensionInfoImpl;
import com.fr.swift.query.info.element.metric.GroupMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.element.target.cal.ResultTarget;
import com.fr.swift.query.info.element.target.cal.TargetInfoImpl;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.utils.BusinessTableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/3/15.
 */
public class AvgUtils {

    public static double average(List<Segment> segments, String fieldName) {
        if (segments.isEmpty()) {
            // 说明是结果的平均值过滤，明细过滤没用，这边返回设置平均值的标识。怪怪的
            return NumberAverageFilter.AVG_HOLDER;
        }
        List<AggregatorValue> values = new ArrayList<AggregatorValue>();
        for (Segment segment : segments) {
            Aggregator aggregator = new AverageAggregate();
            int rowCount = segment.getRowCount();
            Column column = segment.getColumn(new ColumnKey(fieldName));
            AggregatorValue value = aggregator.aggregate(
                    new IntListRowTraversal(IntListFactory.createRangeIntList(0, rowCount - 1)), column);
            values.add(value);
        }
        AggregatorValue value = values.get(0);
        Aggregator aggregator = new AverageAggregate();
        for (int i = 1; i < values.size(); i++) {
            value = AggregatorValueUtils.combine(value, values.get(i), aggregator);
        }
        return value.calculate();
    }

    public static double average(String fieldId, String tableName) {
        Aggregator aggregator = AggregatorFactory.createAggregator(AggregatorType.AVERAGE);
        Metric metric = new GroupMetric(0, new SourceKey(fieldId),
                new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(fieldId)), null, aggregator);
        SourceKey sourceKey = new SourceKey(BusinessTableUtils.getSourceIdByTableId(tableName));
        DimensionInfo dimensionInfo = new DimensionInfoImpl(new AllCursor(), new GeneralFilterInfo(new ArrayList<FilterInfo>(0), GeneralFilterInfo.AND), null, new Dimension[0]);
        TargetInfo targetInfo = new TargetInfoImpl(1, Arrays.asList(metric),
                new ArrayList<GroupTarget>(0),
                Arrays.asList(new ResultTarget(0, 0)),
                Arrays.asList(aggregator));
        QueryInfo queryInfo = new GroupQueryInfoImpl(fieldId, sourceKey, dimensionInfo, targetInfo);
        NodeResultSet nodeResultSet = null;
        try {
            nodeResultSet = (NodeResultSet) QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        } catch (SQLException e) {
            return NumberAverageFilter.AVG_HOLDER;
        }
        return nodeResultSet.getNode().getAggregatorValue()[0].calculate();
    }
}
