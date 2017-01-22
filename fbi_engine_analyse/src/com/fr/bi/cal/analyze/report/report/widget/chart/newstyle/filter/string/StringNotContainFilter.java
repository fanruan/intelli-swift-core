package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class StringNotContainFilter implements IFilter{

    private String value;

    public StringNotContainFilter(String value) {
        this.value = value;
    }

    private boolean isStringNotContain(String target){
        if (this.value == null && target == null) {
            return true;
        }
        if (this.value == null || target == null) {
            return false;
        }
        return !target.contains(this.value);
    }


    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isStringNotContain(array.getString(i))){
                result.put(array.getString(i));
            }
        }
        return result;
    }
}
