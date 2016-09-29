package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class StringStartWithFilter implements IFilter{

    private String start;

    public StringStartWithFilter(String end) {
        this.start = end;
    }

    private boolean isStringStartWith(String value){
        if (value == null && this.start == null) {
            return true;
        }
        if (value == null || this.start == null) {
            return false;
        }
        return value.startsWith(this.start);
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isStringStartWith(array.getString(i))){
                result.put(array.getString(i));
            }
        }
        return result;
    }
}
