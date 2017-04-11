package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/20.
 */
public class VanStackedRadarWidget extends VanRadarWidget{

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("columnType", true);

        return plotOptions;
    }

}
