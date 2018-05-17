package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class TargetPercentCalculator extends AbstractTargetCalculator{
    public TargetPercentCalculator(int paramIndex, int resultIndex, Iterator<Iterator<List<AggregatorValue[]>>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() throws Exception {
        while (iterators.hasNext()) {
            Iterator<List<AggregatorValue[]>> iterator = iterators.next();
            List<List<AggregatorValue[]>> rows = new ArrayList<List<AggregatorValue[]>>();
            Double[] values = null;
            while (iterator.hasNext()) {
                List<AggregatorValue[]> row = iterator.next();
                rows.add(row);
                if (values == null) {
                    values = row.isEmpty() ? null : new Double[row.size()];
                }
                for (int i = 0; i < row.size(); i++) {
                    Double v = row.get(i)[paramIndex].calculate();
                    // 跳过空值
                    if (Double.isNaN(v)) {
                        continue;
                    }
                    if (values[i] == null) {
                        values[i] = v;
                        continue;
                    }
                    values[i] += v;
                }
            }
            for (List<AggregatorValue[]> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    Double v = row.get(i)[paramIndex].calculate();
                    if (Double.isNaN(v)) {
                        continue;
                    }
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(v / values[i]);
                }
            }
        }
        return null;
    }
}
