package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanStackedColumnWidget extends VanColumnWidget{

    protected boolean isStacked(String dimensionID){
        return true;
    }

    protected String getStackedKey(String dimensionID) {
        return this.getUsedTargetID()[0];
    }

    public String getStackedKey(String dimensionID, String seriesName){

        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        return item == null ?  this.getStackedKey(dimensionID) : item.optString("index");
    }

}
