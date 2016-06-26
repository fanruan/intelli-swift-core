/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BIServerUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public abstract class RowCalculatorOperator extends AbstractAddColumnOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1094437940677911376L;
    public static final String XML_TAG = "RowCalculatorOperator";
	@BICoreField
    protected BIKey[] dimension;
	@BICoreField
    protected BIKey key;

    protected RowCalculatorOperator(){
    	super();
    	columnType = DBConstant.COLUMN.NUMBER;
    }

	public JSONObject createJSON() throws Exception {
		JSONObject jo =  super.createJSON();
		jo.put("add_column_type", getAddColumnType());
		JSONObject item = new JSONObject();
		item.put("field", this.key.getKey());
		if(this.dimension != null){
			JSONArray group = new JSONArray();
			for (BIKey key : this.dimension){
				group.put(key.getKey());
			}
			item.put("group", group);
		}
		jo.put("item", item);
		return jo;
	}

	@Override
	public void parseJSON(JSONObject jsonObject) throws Exception {
		super.parseJSON(jsonObject);
		JSONObject item = jsonObject.getJSONObject("item");
		this.key = new IndexKey(item.getString("field"));
		if (item.has("group")){
			JSONArray ja = item.getJSONArray("group");
			this.dimension = new IndexKey[ja.length()];
			for (int i = 0; i < ja.length(); i++){
				this.dimension[i] = new IndexKey(ja.getString(i));
			}
		}
	}


	@Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
		 int rowCount = ti.getRowCount();
		 ResultDealer dealer = createResultDealer(travel, startCol);
		 ResultDealer dimensionDealer = BIServerUtils.createDimensonDealer(this.dimension, dealer);
		 dimensionDealer.dealWith(ti, ti.getAllShowIndex());
		 return rowCount;
	}


	/**
	 * @return
	 */
	protected abstract ResultDealer createResultDealer(Traversal<BIDataValue> travel, int startCol);

	protected abstract String getAddColumnType();
}