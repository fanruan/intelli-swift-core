package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanDonutWidget extends VanPieWidget{

    public JSONObject createPlotOptions() throws JSONException {
        JSONObject plotOptions = super.createPlotOptions();

        plotOptions.put("innerRadius", "50%");

        return plotOptions;
    }

}
