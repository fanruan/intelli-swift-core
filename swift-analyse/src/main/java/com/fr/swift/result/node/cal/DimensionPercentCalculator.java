package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class DimensionPercentCalculator extends AbstractTargetCalculator {
    public DimensionPercentCalculator(int paramIndex, int resultIndex, Iterator<Iterator<List<AggregatorValue[]>>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() throws Exception {
        while (iterators.hasNext()) {
            Iterator<List<AggregatorValue[]>> iterator = iterators.next();
            while (iterator.hasNext()) {
                List<AggregatorValue[]> row = iterator.next();
                for (int i = 0; i < row.size(); i++) {
                    Double value = row.get(i)[paramIndex].calculate();
                    // 跳过空值
                    if (Double.isNaN(value)) {
                        continue;
                    }
                    Double sum = getSum(row.get(i));
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(value / sum);
                }
            }
        }
        return null;
    }

    private Double getSum(AggregatorValue[] aggregatorValues) {
        double d = 0;
        for (AggregatorValue value : aggregatorValues){
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
