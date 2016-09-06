package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class BIAbstractChartSetting implements BIChartSetting {
    @Override
    public JSONObject formatItems() {
        return new JSONObject();
    }

    @Override
    public JSONObject formatConfig() {
        return new JSONObject();
    }


}
