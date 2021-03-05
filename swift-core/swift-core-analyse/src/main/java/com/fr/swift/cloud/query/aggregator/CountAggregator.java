package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * @author pony
 * @date 2018/3/26
 */
public class CountAggregator extends SingleColumnAggregator<DoubleAmountAggregatorValue> implements Serializable {

    protected static final CountAggregator INSTANCE = new CountAggregator();
    private static final long serialVersionUID = 4423225450628417261L;

    @Override
    public DoubleAmountAggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
        DoubleAmountAggregatorValue value = new DoubleAmountAggregatorValue();
        value.setValue(traversal.getCardinality());
        return value;
    }

    @Override
    public DoubleAmountAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return new DoubleAmountAggregatorValue(value.calculate());
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.COUNT;
    }

    @Override
    public void combine(DoubleAmountAggregatorValue value, DoubleAmountAggregatorValue other) {
        value.setValue(value.getValue() + other.getValue());
    }
}
