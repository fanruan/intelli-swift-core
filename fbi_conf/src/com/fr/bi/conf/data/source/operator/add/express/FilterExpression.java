/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.filter.RowFilter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class FilterExpression<T> implements Expression {
	
	private RowFilter<T> filter;
	private BIKey key;
	private Object value;

	@Override
	public void parseJSON(JSONObject jo) throws Exception {
		key = new IndexKey(jo.getString("field"));
		value = jo.get("value");
	}

	@Override
	public JSONObject createJSON() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BICore fetchObjectCore() {
		return null;
	}

	@Override
	public Object get(ICubeTableService ti, int row) {
		Object v = ti.getRow(key, row);
		if(filter.isMatchValue((T) v)){
			return value;
		}
		return null;
	}

}