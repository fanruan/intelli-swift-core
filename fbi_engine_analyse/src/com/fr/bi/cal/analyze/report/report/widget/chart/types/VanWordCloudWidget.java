package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONObject;

/**
 * Created by shine on 2017/7/10.
 */
public class VanWordCloudWidget extends VanChartWidget {

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {

        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);


        plotOptions.put("minFontSize", settings.optInt("minFontSize"));
        plotOptions.put("maxFontSize", settings.optInt("maxFontSize"));

        plotOptions.put("fontFamily", "Microsoft YaHei");
        plotOptions.put("minRotation", 0);
        plotOptions.put("maxRotation", 0);

        return plotOptions;
    }

    protected String categoryKey(){
        return "name";
    }

    protected String valueKey(){
        return "value";
    }

    @Override
    public String getSeriesType(String dimensionID) {
        return "wordCloud";
    }
}
