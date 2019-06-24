package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 所有值的汇总值的计算指标。感觉这个计算指标有点多余。显示汇总行已经汇总值了。
 * Created by Lyon on 2018/4/4.
 */
public class SumOfAllCalculator extends AbstractTargetCalculator {

    private Double[] summaryValue;

    public SumOfAllCalculator(int paramIndex, int resultIndex,
                              Iterator<Iterator<AggregatorValueSet>> iterators, Double[] summaryValue) {
        super(paramIndex, resultIndex, iterators);
        this.summaryValue = summaryValue;
    }

    @Override
    public Object call() {
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
                    AggregatorValueRow valueRow = row.next();
                    Double v = valueRow.getValue(paramIndex).calculate();
                    // 跳过空值
                    if (Double.isNaN(v)) {
                        continue;
                    }
                    if (values[i] == null) {
                        values[i] = v;
                        continue;
                    }
                    values[i] += v;
                    i++;
                }
                row.reset();
            }
            for (AggregatorValueSet row : rows) {
                int i = 0;
                while (row.hasNext()) {
                    row.next().setValue(resultIndex, new DoubleAmountAggregatorValue(values[i++]));
                }
                row.reset();
            }
        }
        return null;
    }
}
