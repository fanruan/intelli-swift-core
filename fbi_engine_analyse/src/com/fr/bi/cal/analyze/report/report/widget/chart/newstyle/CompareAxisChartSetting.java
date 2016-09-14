package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONException;

/**
 * Created by User on 2016/8/31.
 */
public class CompareAxisChartSetting extends BIAbstractCompareChart {

    public CompareAxisChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "column";
    }
}
