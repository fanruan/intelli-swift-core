package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class SumOfAboveCalculator extends AbstractTargetCalculator {

    public SumOfAboveCalculator(int paramIndex, int resultIndex, Iterator<Iterator<List<AggregatorValue[]>>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<List<AggregatorValue[]>> iterator = iterators.next();
            List<AggregatorValue[]> lastRow = null;
            while (iterator.hasNext()) {
                List<AggregatorValue[]> row = iterator.next();
                for (int i = 0; i < row.size(); i++) {
                    Double lastSum = lastRow == null ? 0 : lastRow.get(i)[resultIndex].calculate();
                    Double value = row.get(i)[paramIndex].calculate();
                    // 跳过空值
                    if (Double.isNaN(value)) {
                        continue;
                    }
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(lastSum + value);
                }
                lastRow = row;
            }
        }
        return null;
    }
}
