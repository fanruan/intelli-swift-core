/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.accumulate;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.RowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.json.JSONObject;

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
    @BICoreField
    private int rule = BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE;

    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject item = jo.getJSONObject("item");
        item.put("rule", this.rule);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        this.rule = item.getInt("rule");
        if (this.rule == BIReportConstant.TARGET_TYPE.SUM_OF_ABOVE){
            this.dimension = null;
        }
    }

	@Override
	protected ResultDealer createResultDealer(Traversal<BIDataValue> travel) {
		return new AccumulateResultDealer(key, travel);
	}

	@Override
	protected String getAddColumnType() {
		return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_ACC;
	}

	@Override
	public String xmlTag() {
		return XML_TAG;
	}

}