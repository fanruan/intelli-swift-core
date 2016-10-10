package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class NumberLargeThanOrEqualFilter implements IFilter{

    public NumberLargeThanOrEqualFilter() {
    }

    private double getNumberAvg(JSONArray array) throws JSONException{
        if(array.length() == 0){
            return Double.NaN;
        }
        double sum = 0;
        for(int i = 0; i < array.length(); i++){
            sum += array.getDouble(i);
        }
        return sum / array.length();
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        double avgValue = this.getNumberAvg(array);
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(array.getDouble(i) >= avgValue){
                result.put(array.getDouble(i));
            }
        }
        return result;
    }
}
