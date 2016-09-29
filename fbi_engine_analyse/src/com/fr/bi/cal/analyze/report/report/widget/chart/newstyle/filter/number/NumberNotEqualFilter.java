package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

import java.math.BigDecimal;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class NumberNotEqualFilter implements IFilter{

    private double value;

    public NumberNotEqualFilter(double value) {
        this.value = value;
    }

    private boolean isNumberNotEqual(double v){
        if (Double.isNaN(v)) {
            return false;
        }
        BigDecimal v1 = new BigDecimal(v);
        BigDecimal v2 = new BigDecimal(value);
        return v1.compareTo(v2) != 0;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isNumberNotEqual(array.getDouble(i))){
                result.put(array.getDouble(i));
            }
        }
        return result;
    }
}
