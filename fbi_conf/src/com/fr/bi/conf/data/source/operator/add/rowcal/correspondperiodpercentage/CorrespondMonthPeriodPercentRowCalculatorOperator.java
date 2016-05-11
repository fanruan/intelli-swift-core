package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod.CorrespondMonthPPPPPPeriodRowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * Created by 小灰灰 on 2016/5/9.
 */
public class CorrespondMonthPeriodPercentRowCalculatorOperator extends CorrespondMonthPPPPPPeriodRowCalculatorOperator {
    @BICoreField
    private static final String XML_TAG="CorrespondMonthPeriodPercentRowCalculatorOperator";
    @Override
    protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
        return new CorrespondPeriodPercentResultDealer(key, travel, periodKey);
    }
    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    protected String getAddColumnType() {
        return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT;
    }

}
