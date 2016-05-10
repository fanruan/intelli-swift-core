package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;

/**
 * Created by 小灰灰 on 2016/5/9.
 */
public class CorrespondMonthPeriodPercentRowCalculatorOperator extends CorrespondPeriodPercentRowCalculatorOperator{
    @BICoreField
    private static final String XML_TAG="CorrespondMonthPeriodPercentRowCalculatorOperator";
    @BICoreField
    private BIKey monthKey;

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

}
