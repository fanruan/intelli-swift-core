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

	}

	@Override
	public JSONObject createJSON() throws Exception {
		// TODO Auto-generated method stub
		return null;
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