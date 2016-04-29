/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.rank;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.RowCalculatorOperator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;

/**
 * @author Daniel
 *
 */
public class RankRowCalculatorOperator extends RowCalculatorOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 418425731949464237L;
	@BICoreField
	private static final String XML_TAG = "RankRowCalculatorOperator";
	@BICoreField
	private int type = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC;

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new RankDealer(key, type, travel);
	}
	
	@Override
	protected int getClassType(){
		return DBConstant.CLASS.INTEGER;
	}

}