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
public abstract class AbstractBrotherTargetCalculator extends AbstractRelationTargetCalculator {
    public AbstractBrotherTargetCalculator(int[] paramIndex, int resultIndex, GroupNode groupNode, List<Map<Integer, Object>> dic, Function<GroupNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex, resultIndex, groupNode, dic, aggFunc, brotherGroupIndex);
    }

    @Override
    protected void initIndexTypePair(List<Pair<Integer, GroupType>> brotherGroupIndex) {
        //环比就是取兄弟最小的那个
        for (Pair<Integer, GroupType> brother : brotherGroupIndex){
            GroupType minType = getMinGranularity(brother.getValue());
            if (decType == null){
                decType = minType;
            }
            if (minType != null && decType.compareTo(minType) <=0){
                decrease = brother;
            }
        }
    }

    private GroupType getMinGranularity(GroupType type) {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK_OF_YEAR:
            case WEEK:
            case DAY:
                return type;
            case Y_M:
                return GroupType.MONTH;
            case Y_Q:
                return GroupType.QUARTER;
            case Y_D:
                return GroupType.DAY;
            case Y_W:
                return GroupType.WEEK_OF_YEAR;
            case Y_M_D:
                return GroupType.DAY;

        }
        return null;
    }

}
