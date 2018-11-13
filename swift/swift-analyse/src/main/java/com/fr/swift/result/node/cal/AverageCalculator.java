package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class AverageCalculator extends AbstractTargetCalculator {

    public AverageCalculator(int paramIndex, int resultIndex, Iterator<Iterator<List<AggregatorValue[]>>> iterators) {
        super(paramIndex, resultIndex, iterators);
    }

    @Override
    public Object call() {
        while (iterators.hasNext()) {
            Iterator<List<AggregatorValue[]>> iterator = iterators.next();
            List<List<AggregatorValue[]>> rows = new ArrayList<List<AggregatorValue[]>>();
            // 交叉表的一行，一个配置计算要计算row.size()次
            Double[] sum = null;
            while (iterator.hasNext()) {
                List<AggregatorValue[]> row = iterator.next();
                rows.add(row);
                if (sum == null) {
                    sum = row.isEmpty() ? null : new Double[row.size()];
                }
                for (int i = 0; i < row.size(); i++) {
                    sum[i] = sum[i] == null ? .0 : sum[i];
                    // 空值怎么处理呢？
                    sum[i] += row.get(i)[paramIndex].calculate();
                }
            }
            Double[] average = new Double[sum.length];
            for (int i = 0; i < average.length; i++) {
                average[i] = rows.isEmpty() ? .0 : sum[i] / rows.size();
            }
            for (List<AggregatorValue[]> row : rows) {
                for (int i = 0; i < average.length; i++) {
                    row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(average[i]);
                }
            }
        }
        return null;
    }
}
