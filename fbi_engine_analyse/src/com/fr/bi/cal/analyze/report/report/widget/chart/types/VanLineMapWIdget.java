package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;

/**
 * Created by shine on 2017/7/10.
 */
public class VanLineMapWIdget extends VanChartWidget{
    @Override
    public String getSeriesType(String dimensionID) {
        return "lineMap";
    }
}
