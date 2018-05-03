package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.GroupTargetImpl;

/**
 * Created by Lyon on 2018/5/3.
 */
public class GroupTargetFactory {

    public static GroupTarget createFromCalTarget(int calTargetType, int queryIndex, int[] paramIndexes, int resultIndex) {
        switch (calTargetType) {
            case BIDesignConstants.DESIGN.CAL_TARGET.FORMULA:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ABOVE);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ABOVE_IN_GROUP:
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_SUM:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_AVG:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_AVG);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MAX:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MAX);
            case BIDesignConstants.DESIGN.CAL_TARGET.SUM_OF_ALL_MIN:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_MIN);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_ASC:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_ASC);
            case BIDesignConstants.DESIGN.CAL_TARGET.RANK_DES:
                return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_RANK_DEC);
        }
        return new GroupTargetImpl(queryIndex, resultIndex, paramIndexes, CalTargetType.ALL_SUM_OF_ALL);
    }

    public static GroupTarget createFromRapidTarget(int rapidCalTargetType, int queryIndex,
                                                    int[] paramIndexes, int resultIndex) {
        switch (rapidCalTargetType) {

        }
        return null;
    }
}
