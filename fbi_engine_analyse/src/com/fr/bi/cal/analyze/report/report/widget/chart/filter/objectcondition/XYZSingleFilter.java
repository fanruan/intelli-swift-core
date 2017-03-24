package com.fr.bi.cal.analyze.report.report.widget.chart.filter.objectcondition;

import com.fr.bi.cal.analyze.report.report.widget.chart.filter.IFilter;
import com.fr.bi.cal.analyze.report.report.widget.chart.filter.FilterValueFactory;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class XYZSingleFilter extends AbstractXYZFilter {

    private String key;
    private IFilter filter;

    public XYZSingleFilter(JSONObject filterValue) throws JSONException{
        filter = FilterValueFactory.parseFilter(filterValue);
        key = filterValue.getString("key");
    }

    @Override
    public String getKey() {
        return this.key;
    }

    private static JSONArray pluck(JSONArray array, String key) throws JSONException{
        JSONArray res = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            res.put(obj.optString(key, ""));
        }
        return res;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray filter = this.filter.getFilterResult(pluck(array, this.key));
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            JSONObject item = array.getJSONObject(i);
            for(int j = 0; j < filter.length(); j++){
                if(filter.optDouble(j) == item.getDouble(this.key)){
                    result.put(item);
                    break;
                }
            }
        }
        return result;
    }
}
