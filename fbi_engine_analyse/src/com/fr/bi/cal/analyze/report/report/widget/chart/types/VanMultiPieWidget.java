package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/22.
 */
public class VanMultiPieWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject data) throws Exception{


        return JSONArray.create();
    }

    public String getSeriesType(String dimensionID){
        return "multiPie";
    }

}
