package com.fr.swift.adaptor.transformer.cal;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.query.aggregator.AverageAggregate;
import com.fr.swift.query.filter.detail.impl.number.NumberAverageFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;

import java.util.ArrayList;
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
}
