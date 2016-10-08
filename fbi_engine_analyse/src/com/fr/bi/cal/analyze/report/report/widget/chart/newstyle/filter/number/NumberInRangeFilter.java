package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class NumberInRangeFilter implements IFilter{

    private double min;
    private boolean closemin;
    private double max;
    private boolean closemax;

    public NumberInRangeFilter(JSONObject range) throws JSONException{
        if (range.has("min")) {
            try {
                min = range.getDouble("min");
            } catch (Exception e) {
                min = Double.NEGATIVE_INFINITY;
            }
        } else {
            min = Double.NEGATIVE_INFINITY;
        }
        if (range.has("closemin")) {
            this.closemin = range.getBoolean("closemin");
        }
        if (range.has("max")) {
            try {
                max = range.getDouble("max");
            } catch (Exception e) {
                max = Double.POSITIVE_INFINITY;
            }

        } else {
            max = Double.POSITIVE_INFINITY;
        }
        if (range.has("closemax")) {
            this.closemax = range.getBoolean("closemax");
        }

    }

    private boolean matchValue(double v) {
        return (closemin ? v >= min : v > min) && (closemax ? v <= max : v < max);
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(matchValue(array.getDouble(i))){
                result.put(array.getDouble(i));
            }
        }
        return result;
    }
}
