package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public class AllDataCalculator {
    private Aggregator aggregator;

    public AllDataCalculator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public Double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if (segments.length > 0 && traversal.length > 0 && columnKey != null) {
            AggregatorValue aggregatorValue = null;
            for (int i = 0; i < segments.length; i++) {
                if (traversal[i] != null) {
                    AggregatorValue otherValue = aggregator.aggregate(traversal[i], segments[i].getColumn(columnKey));
                    if (aggregatorValue == null) {
                        aggregatorValue = otherValue;
                    } else {
                        aggregator.combine(aggregatorValue, otherValue);
                    }
                }
            }
            return (Double) aggregatorValue.calculateValue();
        }
        return 0d;
    }
}
