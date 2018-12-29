package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Xiaolei.liu
 */

public class DistinctAggregate implements Aggregator<DistinctCountAggregatorValue> {

    protected static final Aggregator INSTANCE = new DistinctAggregate();
    private static final long serialVersionUID = -6095081218942328474L;

    @Override
    public DistinctCountAggregatorValue aggregate(RowTraversal traversal, final Column column) {

        DistinctCountAggregatorValue distinctCount = new DistinctCountAggregatorValue();
        final Set set = new HashSet();
        traversal.traversal(new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {
                set.add(column.getDictionaryEncodedColumn().getValueByRow(row));
            }
        });
        distinctCount.setBitMap(set);
        return distinctCount;
    }

    @Override
    public DistinctCountAggregatorValue createAggregatorValue(AggregatorValue value) {
        return new DistinctCountAggregatorValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.DISTINCT;
    }

    @Override
    public void combine(DistinctCountAggregatorValue value, DistinctCountAggregatorValue other) {
        value.getBitMap().addAll(other.getBitMap());
    }
}
