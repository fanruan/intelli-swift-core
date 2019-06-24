package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AverageCalculator extends AbstractTargetCalculator {

    public AverageCalculator(int paramIndex, int resultIndex, Iterator<Iterator<AggregatorValueSet>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<AggregatorValueSet> iterator = iterators.next();
            List<AggregatorValueSet> rows = new ArrayList<AggregatorValueSet>();
            // 交叉表的一行，一个配置计算要计算row.size()次
            Double[] sum = null;
            while (iterator.hasNext()) {
                AggregatorValueSet row = iterator.next();
                rows.add(row);
                if (sum == null) {
                    sum = row.isEmpty() ? new Double[0] : new Double[row.size()];
                }
                int i = 0;
                while (row.hasNext()) {
                    sum[i] = sum[i] == null ? .0 : sum[i];
                    // 空值怎么处理呢？
                    sum[i++] += row.next().getValue(paramIndex).calculate();
                }
                row.reset();
            }
            Double[] average = new Double[sum.length];
            for (int i = 0; i < average.length; i++) {
                average[i] = rows.isEmpty() ? .0 : sum[i] / rows.size();
            }
            for (AggregatorValueSet row : rows) {
                int i = 0;
                while (row.hasNext()) {
                    row.next().setValue(resultIndex, new DoubleAmountAggregatorValue(average[i++]));
                }
                row.reset();
            }
        }
        return null;
    }
}
