/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.alldata;

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
public class AllDataRowCalculatorOperator extends RowCalculatorOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 317735765472590336L;
	@BICoreField
	public static final String XML_TAG = "AllDataRowCalculatorOperator";
	@BICoreField
	private int summaryType = BIReportConstant.SUMMARY_TYPE.SUM;

	@Override
	public String xmlTag() {
		return XML_TAG;
	}
	
	private static AllDataCalculator createCalculator(int type){
		switch(type){
    	case BIReportConstant.SUMMARY_TYPE.SUM :{
    		 return SumCalculator.INSTANCE;
    	}
    	case BIReportConstant.SUMMARY_TYPE.MAX :{
    		return MaxCalculator.INSTANCE;
    	}
    	case BIReportConstant.SUMMARY_TYPE.MIN :{
    		return MinCalculator.INSTANCE;
    	}
    	case BIReportConstant.SUMMARY_TYPE.COUNT :{
    		return CountCalculator.INSTANCE;
    	}
    	case BIReportConstant.SUMMARY_TYPE.AVG :{
    		return AVGCalculator.INSTANCE;
    	}
    	default : {
    		 return SumCalculator.INSTANCE;
    	}
	}
	}

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		AllDataCalculator cal = createCalculator(summaryType);
		return new CalResultDealer(key, cal, travel);
	}

	@Override
	protected int getClassType(){
		return DBConstant.CLASS.DOUBLE;
	}
}