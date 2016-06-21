/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.alldata;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.RowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.json.JSONObject;

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
	private int rule = BIReportConstant.TARGET_TYPE.SUM_OF_ALL;

	@Override
	public String xmlTag() {
		return XML_TAG;
	}


	public JSONObject createJSON() throws Exception {
		JSONObject jo =  super.createJSON();
		JSONObject item = jo.getJSONObject("item");
		item.put("sum_type", this.summaryType);
		item.put("rule", this.rule);
		return jo;
	}

	@Override
	public void parseJSON(JSONObject jsonObject) throws Exception {
		super.parseJSON(jsonObject);
		JSONObject item = jsonObject.getJSONObject("item");
		this.summaryType = item.getInt("sum_type");
		this.rule = item.getInt("rule");
		if (this.rule == BIReportConstant.TARGET_TYPE.SUM_OF_ALL){
			this.dimension = null;
		}
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
	protected String getAddColumnType() {
		return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_SUM;
	}

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		AllDataCalculator cal = createCalculator(summaryType);
		return new CalResultDealer(key, cal, travel);
	}

	@Override
	protected int getSqlType(){
		return java.sql.Types.DOUBLE;
	}
}