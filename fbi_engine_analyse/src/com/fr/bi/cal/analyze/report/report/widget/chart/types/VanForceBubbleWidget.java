package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/20.
 */
public class VanForceBubbleWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject data) throws JSONException {
        return createXYSeries(data);
    }

    public String getSeriesType(){
        return "forceBubble";
    }
}
