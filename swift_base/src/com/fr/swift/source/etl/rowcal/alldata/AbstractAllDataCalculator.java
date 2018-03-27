package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public abstract class AbstractAllDataCalculator implements AllDataCalculator {
    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        Aggregator aggregator = getAggregator();
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            AggregatorValue averageValue = aggregator.aggregate(traversal[0], segments[0].getColumn(columnKey));
            for (int i = 1; i < segments.length; i++) {
                AggregatorValue otherValue = aggregator.aggregate(traversal[i], segments[i].getColumn(columnKey));
                aggregator.combine(averageValue, otherValue);
            }
            return averageValue.calculate();
        }
        return 0;
    }

    protected abstract Aggregator getAggregator();
}
