package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.conf.session.BISessionProvider;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanDonutWidget extends VanPieWidget{

    public JSONObject createPlotOptions(BISessionProvider session, JSONObject settings) throws Exception {
        JSONObject plotOptions = super.createPlotOptions(session, settings);

        plotOptions.put("innerRadius", "50%");

        return plotOptions;
    }

}
