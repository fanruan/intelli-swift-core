package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class MaxOrMinCalculator extends AbstractTargetCalculator {

    private Comparator<Double> comparator;

    public MaxOrMinCalculator(int paramIndex, int resultIndex,
                              Iterator<Iterator<AggregatorValueSet>> iterators, boolean isMax) {
        super(paramIndex, resultIndex, iterators);
        this.comparator = isMax ? Comparators.<Double>asc() : Comparators.<Double>desc();
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            List<AggregatorValueSet> rows = new ArrayList<AggregatorValueSet>();
            Double[] values = null;
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                Iterator<AggregatorValueRow> rowIt = row.iterator();
                rows.add(row);
                if (values == null) {
                    values = row.isEmpty() ? null : new Double[row.size()];
                }
                int i = 0;
                while (rowIt.hasNext()) {
                    Double v = rowIt.next().getValue(paramIndex).calculate();
                    // 跳过空值
                    if (Double.isNaN(v)) {
                        continue;
                    }
                    if (values[i] == null) {
                        values[i] = v;
                        continue;
                    }
                    if (comparator.compare(values[i], v) < 0) {
                        values[i++] = v;
                    }
                }
            }
            for (AggregatorValueSet row : rows) {
                int i = 0;
                for (AggregatorValueRow aggregatorValueRow : row) {
                    aggregatorValueRow.setValue(resultIndex, new DoubleAmountAggregatorValue(values[i++]));
                }
            }
        }
        return null;
    }
}
