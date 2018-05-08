package com.fr.swift.adaptor.transformer.cal;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.query.aggregator.AverageAggregate;
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
        if (segments.size() < 1) {
            throw new IllegalArgumentException();
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
