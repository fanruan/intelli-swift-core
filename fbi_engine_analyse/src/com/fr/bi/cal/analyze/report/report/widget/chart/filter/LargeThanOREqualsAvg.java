package com.fr.bi.cal.analyze.report.report.widget.chart.filter;

import com.fr.bi.field.filtervalue.number.nonefilter.NumberLargeThanOREqualsAvgFilterValue;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/5/26.
 */
public class LargeThanOREqualsAvg extends NumberLargeThanOREqualsAvgFilterValue {


    double average;

    public LargeThanOREqualsAvg(JSONObject filterValue, long userID, JSONArray data){

        double total = 0;
        for(int i = 0, len = data.length(); i < len; i++){
            total += data.optJSONObject(i).optDouble("y");
        }
        this.average = total/data.length();
    }

    public boolean isMatchValue(Number value) {
        return value.doubleValue() >= this.average;
    }
}
