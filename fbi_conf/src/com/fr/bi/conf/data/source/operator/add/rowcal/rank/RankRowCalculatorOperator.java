/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.rank;

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
public class RankRowCalculatorOperator extends RowCalculatorOperator {

	/**`
	 * 
	 */
	private static final long serialVersionUID = 418425731949464237L;
	@BICoreField
	private static final String XML_TAG = "RankRowCalculatorOperator";
	@BICoreField
	private int type = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC;
	@BICoreField
	private int rule = BIReportConstant.TARGET_TYPE.RANK;
	public JSONObject createJSON() throws Exception {
		JSONObject jo =  super.createJSON();
		JSONObject item = jo.getJSONObject("item");
		item.put("sortType", this.type);
		item.put("rule", this.rule);
		return jo;
	}

	@Override
	public void parseJSON(JSONObject jsonObject) throws Exception {
		super.parseJSON(jsonObject);
		JSONObject item = jsonObject.getJSONObject("item");
		this.type = item.getInt("sortType");
		this.rule = item.getInt("rule");
		if (this.rule == BIReportConstant.TARGET_TYPE.RANK){
			this.dimension = null;
		}
	}

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new RankDealer(key, type, travel);
	}

	@Override
	protected String getAddColumnType() {
		return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_RANK;
	}

	@Override
	protected int getClassType(){
		return DBConstant.CLASS.INTEGER;
	}

}