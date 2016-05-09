/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.fr.bi.base.BICore;
import com.finebi.cube.api.ICubeTableService;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class LeftExpression implements Expression {
	
	private boolean isUnitLeft = false;
	
	private Object value;

	@Override
	public void parseJSON(JSONObject jo) throws Exception {
		value = jo.optString("other", null);
		isUnitLeft = jo.getBoolean("showOther");
	}

	@Override
	public JSONObject createJSON() throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("showOther", isUnitLeft);
		if (isUnitLeft){
			jo.put("other", value);
		}
		return jo;
	}

	@Override
	public BICore fetchObjectCore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(ICubeTableService ti, int row) {
		return isUnitLeft ? value : null;
	}

}