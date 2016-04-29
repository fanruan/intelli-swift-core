package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.chart.chartattr.Plot;
import com.fr.json.JSONParser;

public interface ChartStyle extends JSONParser {

    public void dealWithStyle(Plot plot);

}