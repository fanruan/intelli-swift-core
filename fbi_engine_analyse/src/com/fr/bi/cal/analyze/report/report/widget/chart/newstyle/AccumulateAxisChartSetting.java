package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONException;

/**
 * Created by windy on 2016/8/31.
 */
public class AccumulateAxisChartSetting extends BIAbstractAccumulateChartSetting {

    public AccumulateAxisChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "column";
    }
}
