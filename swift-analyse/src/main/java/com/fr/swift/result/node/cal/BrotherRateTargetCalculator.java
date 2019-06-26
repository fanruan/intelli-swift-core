package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/16.
 */
public class BrotherRateTargetCalculator extends AbstractBrotherTargetCalculator {
    public BrotherRateTargetCalculator(int[] paramIndex, int resultIndex, SwiftNode resultSet, List<Map<Integer, Object>> dic, Function<SwiftNode, AggregatorValueSet> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex, resultIndex, resultSet, dic, aggFunc, brotherGroupIndex);
    }

    @Override
    protected Double getValue(Double relationValue, Double currentValue) {
        return currentValue == null ? null : currentValue / relationValue - 1;
    }
}
