package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/16.
 */
public class BrotherValueTargetCalculator extends AbstractBrotherTargetCalculator {
    public BrotherValueTargetCalculator(int[] paramIndex, int resultIndex, GroupNode groupNode, List<Map<Integer, Object>> dic, Function<GroupNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex, resultIndex, groupNode, dic, aggFunc, brotherGroupIndex);
    }

    @Override
    protected Double getValue(Double relationValue, Double currentValue) {
        return relationValue;
    }
}
