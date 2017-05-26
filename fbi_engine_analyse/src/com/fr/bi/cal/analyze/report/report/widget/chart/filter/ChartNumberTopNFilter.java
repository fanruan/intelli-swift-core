package com.fr.bi.cal.analyze.report.report.widget.chart.filter;

import com.fr.bi.field.filtervalue.number.nfilter.NumberTopNFilterValue;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by eason on 2017/5/26.
 */
public class ChartNumberTopNFilter extends NumberTopNFilterValue {

    double[] values;

    public ChartNumberTopNFilter(JSONObject filterValue, long userID, JSONArray data){
        this.parseJSON(filterValue, userID);

        int len = data.length();

        values = new double[len];

        for(int i = 0; i < len; i++){
            values[i] = data.optJSONObject(i).optDouble("y");
        }

        Arrays.sort(values);
    }

    public boolean isMatchValue(Number value) {

        int index = 0, count = this.values.length;

        for(; index < count; index++){
            if(this.values[index] == value.doubleValue()){
                return index + this.n == count;
            }
        }

        return false;
    }

}
