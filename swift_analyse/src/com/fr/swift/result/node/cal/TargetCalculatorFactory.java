package com.fr.swift.result.node.cal;

import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.TargetCalculatorInfo;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.LastDimensionIterator;
import com.fr.swift.result.node.iterator.NodeMapperUtils;
import com.fr.swift.result.node.xnode.XLeftNode;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorFactory {

    public static TargetCalculator create(TargetCalculatorInfo info, GroupNode groupNode) {
        CalTargetType type = info.getType();
        Iterator<List<AggregatorValue[]>> iterator = createIterator(type, groupNode);
        switch (type) {
            case ALL_SUM_OF_ALL: {
                Double[] values;
                if (groupNode instanceof XLeftNode) {
                    List<AggregatorValue[]> aggregatorValues = ((XLeftNode) groupNode).getValueArrayList();
                    values = new Double[aggregatorValues.size()];
                    for (int i = 0; i < aggregatorValues.size(); i++) {
                        values[i] = aggregatorValues.get(i)[info.getParamIndex()].calculate();
                    }
                } else {
                    Double value = groupNode.getAggregatorValue()[info.getParamIndex()].calculate();
                    values = new Double[] { value };
                }
                return new AllSumOfAllCalculator(info.getParamIndex(), info.getResultIndex(), iterator, values);
            }
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

    private static Iterator<List<AggregatorValue[]>> createIterator(CalTargetType type, GroupNode root) {
        switch (type) {
            case ALL_SUM_OF_ALL:
            case ALL_AVG:
            case ALL_SUM_OF_ABOVE:
            case ALL_MAX:
            case ALL_MIN:
            case ALL_RANK_ASC:
            case ALL_RANK_DEC: {
                if (root instanceof XLeftNode) {
                    return new LastDimensionIterator(root, NodeMapperUtils.mapper((XLeftNode) root));
                }
                return new LastDimensionIterator(root, NodeMapperUtils.mapper(root));
            }
        }
        return new LastDimensionIterator(root, NodeMapperUtils.mapper(root));
    }
}
