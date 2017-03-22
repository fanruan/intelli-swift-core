package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanMapWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject originData) throws JSONException {
        JSONArray series = JSONArray.create();
        String type = this.getSeriesType();
        return series;
    }

    public String getSeriesType(){
        return "areaMap";
    }

}
