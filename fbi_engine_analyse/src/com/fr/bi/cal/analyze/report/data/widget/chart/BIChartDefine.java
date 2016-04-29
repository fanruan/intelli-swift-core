package com.fr.bi.cal.analyze.report.data.widget.chart;


import com.fr.chart.base.ChartTypeValueCollection;

public class BIChartDefine {

    private ChartTypeValueCollection chartContent;
    private int chartType = 0;

    public BIChartDefine(ChartTypeValueCollection chartContent, int chartType) {
        this.chartContent = chartContent;
        this.chartType = chartType;
    }

    public int getChartType() {
        return chartType;
    }

    public ChartTypeValueCollection getChartContent() {
        return chartContent;
    }

}