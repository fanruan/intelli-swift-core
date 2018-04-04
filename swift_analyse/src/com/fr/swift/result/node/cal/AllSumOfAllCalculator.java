package com.fr.swift.result.node.cal;

import com.fr.swift.result.TargetGettingKey;

import java.util.Iterator;

/**
 * 所有值的汇总值的计算指标。感觉这个计算指标有点多余。显示汇总行已经汇总值了。
 * Created by Lyon on 2018/4/4.
 */
public class AllSumOfAllCalculator extends AbstractTargetCalculator {

    private Double summaryValue;

    public AllSumOfAllCalculator(TargetGettingKey paramIndex, TargetGettingKey resultIndex,
                                 Iterator<Number[]> iterator, Double summaryValue) {
        super(paramIndex, resultIndex, iterator);
        this.iterator = iterator;
        this.summaryValue = summaryValue;
    }

    @Override
    public Object call() throws Exception {
        while (iterator.hasNext()) {
            Number[] row = iterator.next();
            row[resultIndex.getTargetIndex()] = summaryValue;
        }
        return null;
    }
}
