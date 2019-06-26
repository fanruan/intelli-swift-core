package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.Iterator;

/**
 * Created by pony on 2018/5/16.
 */
public class DimensionPercentCalculator extends AbstractTargetCalculator {
    public DimensionPercentCalculator(int paramIndex, int resultIndex, Iterator<Iterator<AggregatorValueSet>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() throws Exception {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                for (AggregatorValueRow item : row) {
                    Double value = item.getValue(paramIndex).calculate();
                    // 跳过空值
                    if (Double.isNaN(value)) {
                        continue;
                    }
                    Double sum = getSum(item);
                    item.setValue(resultIndex, new DoubleAmountAggregatorValue(value / sum));
                }
            }
        }
        return null;
    }

    private Double getSum(AggregatorValueRow aggregatorValues) {
        double d = 0;
        for (int i = 0; i < aggregatorValues.getSize(); i++) {
            AggregatorValue value = aggregatorValues.getValue(i);
            if (value != null){
                Object result = value.calculateValue();
                if (result != null){
                    d += ((Number)result).doubleValue();
                }
            }
        }
        return d;
    }
}
