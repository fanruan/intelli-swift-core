package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class TargetPercentCalculator extends AbstractTargetCalculator{
    public TargetPercentCalculator(int paramIndex, int resultIndex, Iterator<Iterator<AggregatorValueSet>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() throws Exception {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            List<AggregatorValueSet> rows = new ArrayList<AggregatorValueSet>();
            Double[] values = null;
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                rows.add(row);
                if (values == null) {
                    values = row.isEmpty() ? null : new Double[row.size()];
                }
                int i = 0;
                while (row.hasNext()) {
                    Double v = row.next().getValue(paramIndex).calculate();
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
                row.reset();
            }
            for (AggregatorValueSet row : rows) {
                int i = 0;
                while (row.hasNext()) {
                    AggregatorValueRow next = row.next();
                    Double v = next.getValue(paramIndex).calculate();
                    if (Double.isNaN(v)) {
                        continue;
                    }
                    next.setValue(resultIndex, new DoubleAmountAggregatorValue(v / values[i++]));
                }
                row.reset();
            }
        }
        return null;
    }
}
