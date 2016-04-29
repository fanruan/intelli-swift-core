package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.chart.chartattr.MeterPlot;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.MeterStyle;
import com.fr.json.JSONObject;

public class DashBoardDefaultStyle implements ChartStyle {

    @Override
    public void parseJSON(JSONObject jo) throws Exception {


    }

    @Override
    public void dealWithStyle(Plot plot) {
        if (plot instanceof MeterPlot) {
            MeterStyle style = ((MeterPlot) plot).getMeterStyle();
            style.setDialShape(MeterStyle.PIE270);
            style.setMaxArrowAngle(270);
            style.setMeterAngle(270);
            style.convertAreaColor2List(0);
        }
    }

}