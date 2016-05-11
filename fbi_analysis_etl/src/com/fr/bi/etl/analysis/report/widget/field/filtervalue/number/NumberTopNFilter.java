/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line.TopNLine;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class NumberTopNFilter extends NumberLargeOrEqualsCLFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5225849710130195308L;

	@Override
	public void parseJSON(JSONObject jo , long userId) throws Exception{
		super.parseJSON(jo, userId);
		if(jo.has("filter_value")){
            JSONObject filterValue = jo.getJSONObject("filter_value");
            if (filterValue.has("value")){
                this.getter = new TopNLine(jo.getInt("value"));
            }
		}
	}

}