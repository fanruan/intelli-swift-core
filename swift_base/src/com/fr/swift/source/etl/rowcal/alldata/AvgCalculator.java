package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.AverageAggregate;
import com.fr.swift.query.aggregator.DoubleAverageAggregateValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:33
 */
public class AvgCalculator implements AllDataCalculator {

    public static AvgCalculator INSTANCE = new AvgCalculator();

    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            DoubleAverageAggregateValue averageValue = AverageAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(columnKey));
            for (int i = 1; i < segments.length; i++) {
                DoubleAverageAggregateValue otherValue = AverageAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(columnKey));
                AverageAggregate.INSTANCE.combine(averageValue, otherValue);
            }
            return averageValue.calculate();
        }
        return 0;
    }
}
