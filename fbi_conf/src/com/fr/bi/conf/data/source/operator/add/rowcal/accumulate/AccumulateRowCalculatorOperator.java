/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.accumulate;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.RowCalculatorOperator;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * @author Daniel
 *
 */
public class AccumulateRowCalculatorOperator extends RowCalculatorOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8295114270512302274L;
	@BICoreField
	private static final String XML_TAG = "AccumulateRowCalculatorOperator";
	
	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new AccumulateResultDealer(key, travel);
	}

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

}