package com.fr.bi.field.target.target;

import com.fr.bi.field.target.calculator.sum.StringAppendCalculator;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.report.result.TargetCalculator;

/**
 * Created by 小灰灰 on 2014/10/24.
 */
public class BIStringTarget extends BISummaryTarget {
    @Override
    public int getTargetType() {
        return DBConstant.COLUMN.STRING;
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new StringAppendCalculator(this);
    }
}