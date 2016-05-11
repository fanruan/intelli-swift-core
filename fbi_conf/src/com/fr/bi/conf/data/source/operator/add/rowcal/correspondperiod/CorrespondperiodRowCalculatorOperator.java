/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.RowCalculatorOperator;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * @author Daniel
 *
 */
public class CorrespondperiodRowCalculatorOperator extends RowCalculatorOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 574637556670188056L;
	@BICoreField
	private static final String XML_TAG="CorrespondPeriodRowCalculatorOperator";
	@BICoreField
	private BIKey periodKey;
	
	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new CorrespondPeriodResultDealer(key, travel, periodKey);
	}

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

}
