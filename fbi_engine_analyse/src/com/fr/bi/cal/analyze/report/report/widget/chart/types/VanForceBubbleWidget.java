package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/20.
 */
public class VanForceBubbleWidget extends VanDotWidget{

    protected JSONObject createDataLabels(JSONObject settings) throws JSONException {
        JSONObject dataLabels = JSONObject.create().put("enabled", true)
                .put("formatter", JSONObject.create().put("identifier", VALUE));

        return dataLabels;
    }

    public JSONArray createSeries(JSONObject data) throws Exception {
        return this.createXYSeries(data);
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesTooltipFormat(options);
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesDataLabelFormat(options);
    }


    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE;
    }

    public String getSeriesType(String dimensionID){
        return "forceBubble";
    }
}
