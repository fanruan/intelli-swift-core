/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line.BottomNLine;
import com.fr.json.JSONObject;

/**
 * @author Daniel
 *
 */
public class NumberBottomNFilter extends NumberSmallOrEqualsCLFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7362372295014642285L;

	@Override
	public void parseJSON(JSONObject jo , long userId) throws Exception{
		super.parseJSON(jo, userId);
        if(jo.has("filter_value")){
            JSONObject filterValue = jo.getJSONObject("filter_value");
            if (filterValue.has("value")){
                this.getter = new BottomNLine(filterValue.getInt("value"));
            }
        }
	}

}