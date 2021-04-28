package com.fr.swift.cloud.result.node.cal;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.group.GroupType;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.structure.Pair;
import com.fr.swift.cloud.util.function.Function;

import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/16.
 */
public class BrotherValueTargetCalculator extends AbstractBrotherTargetCalculator {
    public BrotherValueTargetCalculator(int[] paramIndex, int resultIndex, SwiftNode groupNode, List<Map<Integer, Object>> dic, Function<SwiftNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex, resultIndex, groupNode, dic, aggFunc, brotherGroupIndex);
    }

    @Override
    protected Double getValue(Double relationValue, Double currentValue) {
        return relationValue;
    }
}
