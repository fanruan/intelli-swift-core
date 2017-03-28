package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanTreeMapWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject data) throws JSONException {

        return JSONArray.create();
    }

    public String getSeriesType(String dimensionID){
        return "treeMap";
    }


}
