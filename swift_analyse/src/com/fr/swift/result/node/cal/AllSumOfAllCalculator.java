package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;

import java.util.Iterator;
import java.util.List;

/**
 * 所有值的汇总值的计算指标。感觉这个计算指标有点多余。显示汇总行已经汇总值了。
 * Created by Lyon on 2018/4/4.
 */
public class AllSumOfAllCalculator extends AbstractTargetCalculator {

    private Double[] summaryValue;

    public AllSumOfAllCalculator(int paramIndex, int resultIndex,
                                 Iterator<List<AggregatorValue[]>> iterator, Double[] summaryValue) {
        super(paramIndex, resultIndex, iterator);
        this.summaryValue = summaryValue;
    }

    @Override
    public Object call() throws Exception {
        while (iterator.hasNext()) {
            List<AggregatorValue[]> row = iterator.next();
            for (int i = 0; i < row.size(); i++) {
                row.get(i)[resultIndex] = new DoubleAmountAggregatorValue(summaryValue[i]);
            }
        }
        return null;
    }
}
