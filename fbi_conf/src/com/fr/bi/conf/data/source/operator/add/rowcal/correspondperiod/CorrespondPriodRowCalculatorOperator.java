/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.CorrespondRowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * @author Daniel
 *
 */
public class CorrespondPriodRowCalculatorOperator extends CorrespondRowCalculatorOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 574637556670188056L;
	@BICoreField
	private static final String XML_TAG="CorrespondPriodRowCalculatorOperator";


	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new CorrespondPeriodResultDealer(key, travel, periodKey);
	}

	@Override
	protected String getAddColumnType() {
		return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_LP;
	}

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

}