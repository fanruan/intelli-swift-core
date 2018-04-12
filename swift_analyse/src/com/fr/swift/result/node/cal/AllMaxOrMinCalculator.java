package com.fr.swift.result.node.cal;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AllMaxOrMinCalculator extends AbstractTargetCalculator {

    private Comparator<Double> comparator;

    public AllMaxOrMinCalculator(int paramIndex, int resultIndex,
                                 Iterator<List<AggregatorValue[]>> iterator, boolean isMax) {
        super(paramIndex, resultIndex, iterator);
        this.comparator = isMax ? Comparators.<Double>asc() : Comparators.<Double>desc();
    }

    @Override
    public Object call() throws Exception {
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
                if (values[i] == null) {
                    values[i] = v;
                    continue;
                }
                if (comparator.compare(values[i], v) < 0) {
                    values[i] = v;
                }
            }
        }
        for (List<AggregatorValue[]> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(values[i]);
            }
        }
        return null;
    }
}
