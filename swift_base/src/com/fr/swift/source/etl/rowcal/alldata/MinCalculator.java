package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.DoubleAmountAggregateValue;
import com.fr.swift.query.aggregator.MaxAggregate;
import com.fr.swift.query.aggregator.MinAggregate;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:30
 */
public class MinCalculator implements AllDataCalculator {

    public static MinCalculator INSTANCE = new MinCalculator();


    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            DoubleAmountAggregateValue minValue = MinAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(columnKey));
            for (int i = 1; i < segments.length; i++) {
                DoubleAmountAggregateValue otherValue = MaxAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(columnKey));
                MinAggregate.INSTANCE.combine(minValue, otherValue);
            }
            return minValue.calculate();
        }
        return 0;
    }
}
