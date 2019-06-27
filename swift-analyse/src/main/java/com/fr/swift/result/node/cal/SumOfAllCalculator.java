package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
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
                              Iterator<Iterator<List<AggregatorValue[]>>> iterators, Double[] summaryValue) {
        super(paramIndex, resultIndex, iterators);
        this.summaryValue = summaryValue;
    }

    @Override
    public Object call() {
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
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(values[i]);
                }
            }
        }
        return null;
    }
}
