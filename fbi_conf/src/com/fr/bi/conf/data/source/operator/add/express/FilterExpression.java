/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class FilterExpression<T> implements Expression {
	
	private FilterValue<T> filter;
	private BIKey key;
	private Object value;
	private int field_type;

	@Override
	public void parseJSON(JSONObject jo) throws Exception {
		key = new IndexKey(jo.getString("field"));
		field_type = jo.getInt("field_type");
		filter = ExpressionFilterValueFactory.createRowValue(jo, field_type);
		value = jo.get("value");
	}

	@Override
	public JSONObject createJSON() throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("value", value);
		jo.put("field", key.getKey());
		jo.put("field_type", field_type);
		jo.join(filter.createJSON());
		return jo;
	}

	@Override
	public BICore fetchObjectCore() {
		return new BICoreGenerator(this).fetchObjectCore();
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