package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class StringEndWithFilter implements IFilter{

    private String end;

    public StringEndWithFilter(String end) {
        this.end = end;
    }

    private boolean isStringEndWith(String value){
        if (value == null && this.end == null) {
            return true;
        }
        if (value == null || this.end == null) {
            return false;
        }
        return value.endsWith(this.end);
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isStringEndWith(array.getString(i))){
                result.put(array.getString(i));
            }
        }
        return result;
    }
}
