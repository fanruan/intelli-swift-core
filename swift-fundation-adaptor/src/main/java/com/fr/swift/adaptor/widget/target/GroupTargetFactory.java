package com.fr.swift.adaptor.widget.target;

import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.value.FormulaValueBean;
import com.finebi.conf.structure.dashboard.widget.field.WidgetBeanFieldValue;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.adapter.target.cal.GroupFormulaTarget;
import com.fr.swift.query.adapter.target.cal.GroupTargetImpl;

/**
 * Created by Lyon on 2018/5/3.
 */
public class GroupTargetFactory {

    public static GroupTarget createFromCalTarget(int calTargetType, int queryIndex, int[] paramIndexes,
                                                  int resultIndex, WidgetBeanFieldValue value) {
        switch (calTargetType) {
            case BIDesignConstants.DESIGN.CAL_TARGET.FORMULA: {
                String formula = ((FormulaValueBean) value).getValue();
                return new GroupFormulaTarget(queryIndex, resultIndex, paramIndexes, CalTargetType.FORMULA, formula);
            }
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

    // TODO: 2018/5/11 切换汇总方式之后，AggregatorValue转换gg了
    public static GroupTarget createFromRapidTarget(int rapidCalTargetType, int queryIndex,
                                                    int[] paramIndexes, int resultIndex) {
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
        }
        return null;
    }
}
