/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class GeneralExpression implements Expression {
	
	@BICoreField
	private Expression[] expressions;
    @BICoreField
    private LeftExpression leftValues;

	@Override
	public void parseJSON(JSONObject jo) throws Exception {
		leftValues = new LeftExpression();
		leftValues.parseJSON(jo);
		if (jo.has("items")){
			JSONArray ja = jo.getJSONArray("items");
			expressions = new Expression[ja.length()];
			for (int i = 0; i < ja.length(); i ++){
				JSONObject item = ja.getJSONObject(i);
				int type = item.getInt("field_type");
				switch (type){
					case DBConstant.COLUMN.STRING :
						expressions[i] = new FilterExpression<String>();
						break;
					case DBConstant.COLUMN.DATE :
						expressions[i] = new FilterExpression<Long>();
						break;
					default:
						expressions[i] = new FilterExpression<Number>();
						break;
				}
				expressions[i].parseJSON(item);
			}
		}
	}

	@Override
	public JSONObject createJSON() throws Exception {
		JSONObject jo = leftValues.createJSON();
		JSONArray items = new JSONArray();
		jo.put("items", items);
		for (Expression ex : expressions){
			items.put(ex.createJSON());
		}
		return jo;
	}

    @Override
    public BICore fetchObjectCore() {

        try {
            return new BICoreGenerator(this).fetchObjectCore();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }

	@Override
	public Object get(ICubeTableService ti, int row, int columnType) {
		//倒序遍历找到值就结束
		for(int i = expressions.length; i > 0; i --){
			Expression e = expressions[i - 1];
			if(e != null){
				Object v = e.get(ti, row, columnType);
				if(v != null){
					return v;
				}
			}
		}
		return leftValues != null ? leftValues.get(ti, row, columnType) : null;
	}

}