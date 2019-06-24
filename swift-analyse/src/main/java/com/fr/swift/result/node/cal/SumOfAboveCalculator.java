package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
public class SumOfAboveCalculator extends AbstractTargetCalculator {

    public SumOfAboveCalculator(int paramIndex, int resultIndex, Iterator<Iterator<AggregatorValueSet>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            AggregatorValueSet lastRow = null;
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                int i = 0;
                while (row.hasNext()) {
                    Double lastSum = lastRow == null ? 0 : lastRow.next().getValue(resultIndex).calculate();
                    AggregatorValueRow next = row.next();
                    Double value = next.getValue(paramIndex).calculate();
                    // 跳过空值
                    if (Double.isNaN(value)) {
                        continue;
                    }
                    next.setValue(resultIndex, new DoubleAmountAggregatorValue(lastSum + value));
                }
                row.reset();
                if (null != lastRow) {
                    lastRow.reset();
                }
                lastRow = row;
            }
        }
        return null;
    }
}
