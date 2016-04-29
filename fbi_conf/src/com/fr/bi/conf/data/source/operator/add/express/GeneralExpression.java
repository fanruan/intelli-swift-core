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
public class GeneralExpression implements Expression {
	
	
	private Expression[] expressions;
	
	private Expression leftValues;

	@Override
	public void parseJSON(JSONObject jo) throws Exception {
		
	}

	@Override
	public JSONObject createJSON() throws Exception {
		return null;
	}

	@Override
	public BICore fetchObjectCore() {
		return null;
	}

	@Override
	public Object get(ICubeTableService ti, int row) {
		//倒序遍历找到值就结束
		for(int i = expressions.length; i > 0; i --){
			Expression e = expressions[i - 1];
			if(e != null){
				Object v = e.get(ti, row);
				if(v != null){
					return v;
				}
			}
		}
		return leftValues != null ? leftValues.get(ti, row) : null;
	}

}