package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanFunnelWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject data) throws JSONException {
        return this.createXYSeries(data);
    }

    public String getSeriesType(){
        return "funnel";
    }
}
