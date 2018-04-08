package com.fr.swift.result.node.cal;

import java.util.Iterator;

/**
 * 所有值的汇总值的计算指标。感觉这个计算指标有点多余。显示汇总行已经汇总值了。
 * Created by Lyon on 2018/4/4.
 */
public class AllSumOfAllCalculator extends AbstractTargetCalculator {

    private Double summaryValue;

    public AllSumOfAllCalculator(int paramIndex, int resultIndex,
                                 Iterator<Number[]> iterator, Double summaryValue) {
        super(paramIndex, resultIndex, iterator);
        this.summaryValue = summaryValue;
    }

    @Override
    public Object call() throws Exception {
        while (iterator.hasNext()) {
            Number[] row = iterator.next();
            row[resultIndex] = summaryValue;
        }
        return null;
    }
}
