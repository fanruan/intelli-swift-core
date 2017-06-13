package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanDonutWidget extends VanChartWidget {

    public String getSeriesType(String dimensionID){
        return "pie";
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("innerRadius", "50%");

        return plotOptions;
    }

    protected String categoryLabelKey() {
        return SERIES;
    }

    protected String seriesLabelKey() {
        return CATEGORY;
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE + PERCENT;
    }

}
