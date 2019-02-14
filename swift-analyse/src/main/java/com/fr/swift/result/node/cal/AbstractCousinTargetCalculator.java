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
public abstract class AbstractCousinTargetCalculator extends AbstractRelationTargetCalculator {
    public AbstractCousinTargetCalculator(int[] paramIndex, int resultIndex, GroupNode groupNode, List<Map<Integer, Object>> dic, Function<GroupNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex, resultIndex, groupNode, dic, aggFunc, brotherGroupIndex);
    }

    @Override
    protected void initIndexTypePair(List<Pair<Integer, GroupType>> brotherGroupIndex) {
        for (Pair<Integer, GroupType> brother : brotherGroupIndex) {
            //设置好了同期的情况
            if (brother.getKey() == -1) {
                decType = brother.getValue();
            }
        }
        decType = GroupType.YEAR;
        if (decType != null) {
            for (Pair<Integer, GroupType> brother : brotherGroupIndex) {
                //设置好了同期的情况
                if (brother.getKey() != -1 && containsGranularity(brother.getValue())) {
                    decrease = brother;
                    break;
                }
            }
        }

    }

    private boolean containsGranularity(GroupType type) {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK_OF_YEAR:
            case WEEK:
            case DAY:
                return type == decType;
            case Y_M:
            case Y_M_D:
                return decType == GroupType.YEAR || decType == GroupType.MONTH;
            case Y_Q:
                return decType == GroupType.YEAR || decType == GroupType.QUARTER;
            case Y_D:
                return decType == GroupType.YEAR || decType == GroupType.DAY;
            case Y_W:
                return decType == GroupType.YEAR || decType == GroupType.WEEK_OF_YEAR;
        }
        return false;
    }

}
