package com.fr.swift.result.node.cal;

import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.TargetCalculatorInfo;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.LastDimensionIterator;
import com.fr.swift.result.node.xnode.XLeftNode;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorFactory {

    public static TargetCalculator create(GroupTarget target, GroupNode groupNode) {
        CalTargetType type = target.type();
        Iterator<List<AggregatorValue[]>> iterator = createIterator(type, groupNode);
        switch (type) {
            case ALL_SUM_OF_ALL: {
                Double[] values;
                if (groupNode instanceof XLeftNode) {
                    List<AggregatorValue[]> aggregatorValues = ((XLeftNode) groupNode).getValueArrayList();
                    values = new Double[aggregatorValues.size()];
                    for (int i = 0; i < aggregatorValues.size(); i++) {
                        values[i] = aggregatorValues.get(i)[target.paramIndexes()[0]].calculate();
                    }
                } else {
                    Double value = groupNode.getAggregatorValue()[target.paramIndexes()[0]].calculate();
                    values = new Double[] { value };
                }
                return new AllSumOfAllCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, values);
            }
            case ALL_AVG:
                return new AllAverageCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_SUM_OF_ABOVE:
                return new AllSumOfAboveCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_MAX:
                return new AllMaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_MIN:
                return new AllMaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
            case ALL_RANK_ASC:
                return new AllRankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_RANK_DEC:
                return new AllRankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
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
                    return new LastDimensionIterator(root, xLeftNodeMapper());
                }
                return new LastDimensionIterator(root, groupNodeMapper());
            }
        }
        return new LastDimensionIterator(root, groupNodeMapper());
    }

    private static Function<GroupNode, List<AggregatorValue[]>> groupNodeMapper() {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return Arrays.<AggregatorValue[]>asList(p.getAggregatorValue());
            }
        };
    }

    private static Function<GroupNode, List<AggregatorValue[]>> xLeftNodeMapper() {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return ((XLeftNode) p).getValueArrayList();
            }
        };
    }
}
