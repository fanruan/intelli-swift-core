package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;

/**
 * Created by 小灰灰 on 2016/5/9.
 */
public class CorrespondMonthperiodRowCalculatorOperator extends CorrespondperiodRowCalculatorOperator {
    @BICoreField
    private static final String XML_TAG="CorrespondMonthPeriodRowCalculatorOperator";
    @BICoreField
    private BIKey monthKey;

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

}
