package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanLineWidget extends VanCartesianWidget{

    public String getSeriesType(String dimensionID){
        return "line";
    }


    public JSONObject createPlotOptions(JSONObject globalSetting, JSONObject settings) throws Exception {
        return super.createPlotOptions(globalSetting, settings).put("marker", JSONObject.create().put("symbol", "circle").put("enabled", true).put("radius", 4.5));
    }

    @Override
    public boolean canCompleteMissTime(){
        return true;
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }
}

