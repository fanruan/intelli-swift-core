package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanFallColumnWidget extends VanColumnWidget{

    public JSONArray createSeries(JSONObject originData) throws Exception {
        return addStackedEmptySeries(super.createSeries(originData));
    }

    protected void toLegendJSON(JSONObject options, JSONObject settings) throws JSONException {
        options.put("legend", JSONObject.create().put("enabled", false));
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException{
        return super.parseLeftValueAxis(settings).put("reversed", false);
    }

}
