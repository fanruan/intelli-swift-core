/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod.PeriodRowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * @author Daniel
 * 环期比
 *
 */
public class PeriodPercentRowCalculatorOperator extends PeriodRowCalculatorOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 574637556670188056L;
	private static final String XML_TAG="PeriodPercentRowCalculatorOperator";

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel, int startCol) {
		return new CorrespondPeriodPercentResultDealer(key, travel, periodKey, startCol);
	}


	@Override
	protected String getAddColumnType() {
		return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT;
	}
	@Override
	public String xmlTag() {
		return XML_TAG;
	}

}