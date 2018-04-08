package com.fr.swift.result.node.cal;

import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.LastDimensionIterator;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorFactory {

    public static TargetCalculator create(TargetCalculatorInfo info, GroupNode groupNode) {
        CalTargetType type = info.getType();
        Iterator<Number[]> iterator = createIterator(type, groupNode);
        switch (type) {
            case ALL_SUM_OF_ALL:
                Double value = groupNode.getSummaryValue()[info.getParamIndex()].doubleValue();
                return new AllSumOfAllCalculator(info.getParamIndex(), info.getResultIndex(), iterator, value);
            case ALL_AVG:
                return new AllAverageCalculator(info.getParamIndex(), info.getResultIndex(), iterator);
            case ALL_SUM_OF_ABOVE:
                return new AllSumOfAboveCalculator(info.getParamIndex(), info.getResultIndex(), iterator);
            case ALL_MAX:
                return new AllMaxOrMinCalculator(info.getParamIndex(), info.getResultIndex(), iterator, true);
            case ALL_MIN:
                return new AllMaxOrMinCalculator(info.getParamIndex(), info.getResultIndex(), iterator, false);
            case ALL_RANK_ASC:
                return new AllRankCalculator(info.getParamIndex(), info.getResultIndex(), iterator, true);
            case ALL_RANK_DEC:
                return new AllRankCalculator(info.getParamIndex(), info.getResultIndex(), iterator, false);
        }
        return null;
    }

    private static Iterator<Number[]> createIterator(CalTargetType type, GroupNode root) {
        switch (type) {
            case ALL_SUM_OF_ALL:
            case ALL_AVG:
            case ALL_SUM_OF_ABOVE:
            case ALL_MAX:
            case ALL_MIN:
            case ALL_RANK_ASC:
            case ALL_RANK_DEC:
                return new LastDimensionIterator(root);
        }
        return new LastDimensionIterator(root);
    }
}
