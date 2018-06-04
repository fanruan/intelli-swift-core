package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.cal.BrotherGroupTarget;
import com.fr.swift.query.info.element.target.cal.CalTargetType;
import com.fr.swift.query.info.element.target.cal.GroupTargetImpl;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by Lyon on 2018/5/3.
 */
public class GroupTargetFactory {

    public static GroupTarget createFromRapidTarget(int rapidCalTargetType, int queryIndex,
                                                    int[] paramIndexes, int resultIndex,
                                                    List<Pair<Integer, GroupType>> brotherIndexGroup) {
        return createFromRapidTarget(rapidCalTargetType, queryIndex, false, paramIndexes,
                resultIndex, brotherIndexGroup);
    }

    public static GroupTarget createFromRapidTarget(int rapidCalTargetType, int queryIndex, boolean isRepeatCal,
                                                    int[] paramIndexes, int resultIndex,
                                                    List<Pair<Integer, GroupType>> brotherIndexGroup) {
        switch (rapidCalTargetType) {
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.RANK_ASC:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_ASC);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.RANK_DES:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_DEC);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_SUM:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ABOVE:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ABOVE);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_AVG:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_AVG);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_MAX:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MAX);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_MIN:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MIN);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.RANK_IN_GROUP_ASC:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_RANK_ASC);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.RANK_IN_GROUP_DES:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_RANK_DEC);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_IN_GROUP_SUM:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_SUM_OF_ALL);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_IN_GROUP_AVG:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_AVG);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_IN_GROUP_MAX:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_MAX);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ALL_IN_GROUP_MIN:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_MIN);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.SUM_OF_ABOVE_IN_GROUP:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.GROUP_SUM_OF_ABOVE);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.YEAR_ON_YEAR_VALUE:
                return new BrotherGroupTarget(queryIndex, resultIndex, paramIndexes, CalTargetType.COUSIN_VALUE, brotherIndexGroup);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.YEAR_ON_YEAR_RATE:
                return new BrotherGroupTarget(queryIndex, resultIndex, paramIndexes, CalTargetType.COUSIN_RATE, brotherIndexGroup);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.MONTH_ON_MONTH_VALUE:
                return new BrotherGroupTarget(queryIndex, resultIndex, paramIndexes, CalTargetType.BROTHER_VALUE, brotherIndexGroup);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.MONTH_ON_MONTH_RATE:
                return new BrotherGroupTarget(queryIndex, resultIndex, paramIndexes, CalTargetType.BROTHER_RATE, brotherIndexGroup);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.CURRENT_DIMENSION_PERCENT:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.DIMENSION_PERCENT);
            case BIDesignConstants.DESIGN.RAPID_CALCULATE_TYPE.CURRENT_TARGET_PERCENT:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.TARGET_PERCENT);

        }
        return null;
    }
}
