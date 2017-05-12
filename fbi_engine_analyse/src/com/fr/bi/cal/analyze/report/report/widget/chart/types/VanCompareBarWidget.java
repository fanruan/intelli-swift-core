package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareBarWidget extends VanCompareColumnWidget{

    public boolean isInverted(){
        return true;
    }

    protected void dealYAxisDiffDefaultSettings(JSONObject settings) throws JSONException {
    }

    protected void makeSeriesDataInvert(JSONObject ser) throws JSONException{
        ser.put("yAxis", 0);

        JSONArray datas = ser.optJSONArray("data");
        String valueKey = this.valueKey();
        for (int i = 0, len = datas.length(); i < len; i++) {
            JSONObject point = datas.getJSONObject(i);
            point.put(valueKey, -point.optDouble(valueKey));
        }
    }

    protected String valueFormatFunc(BISummaryTarget dimension, boolean isTooltip) {

        int index = yAxisIndex(dimension.getValue());

        String format = this.valueFormat(dimension, isTooltip);

        return index == 1 ? String.format("function(){return BI.contentFormat(-arguments[0], \"%s\")}", format)
                : String.format("function(){return BI.contentFormat(arguments[0], \"%s\")}", format);
    }

    protected String labelString(int yAxis){
        return "Math.abs(arguments[0])";
    }

}
