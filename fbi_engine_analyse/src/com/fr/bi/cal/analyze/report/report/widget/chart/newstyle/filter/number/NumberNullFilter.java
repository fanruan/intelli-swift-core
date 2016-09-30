package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class NumberNullFilter implements IFilter{

    private boolean isNumberNull(Object o){
        return null == o;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isNumberNull(array.get(i))){
                result.put(array.get(i));
            }
        }
        return result;
    }
}
