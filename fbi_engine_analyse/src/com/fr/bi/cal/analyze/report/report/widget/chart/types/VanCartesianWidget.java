package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/2.
 */
public abstract class VanCartesianWidget extends VanChartWidget {

    public JSONArray createSeries(JSONObject data) throws JSONException {
        return createXYSeries(data);
    }

    public  JSONObject createOptions() throws JSONException{

        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        JSONObject options = super.createOptions();

        options.put("dataSheet", JSONObject.create().put("enabled", settings.optBoolean("showDataTable")));

        if(options.optBoolean("showZoom")){
            options.put("zoom", JSONObject.create().put("zoomTool", JSONObject.create().put("enabled", true)));
        }

        options.put("xAxis", this.parseCategoryAxis(settings));
        options.put("yAxis", this.parseValueAxis(settings));

        JSONObject plotOptions = JSONObject.create();

        return options.put("plotOptions", plotOptions);
    }

}
