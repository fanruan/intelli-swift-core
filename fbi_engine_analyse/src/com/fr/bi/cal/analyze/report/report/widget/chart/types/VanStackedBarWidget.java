package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanStackedBarWidget extends VanStackedColumnWidget{

    public boolean isInverted(){
        return true;
    }


    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException {
        return super.parseLeftValueAxis(settings).put("position", "right").put("reversed", false);
    }

    protected double cateAxisRotation() {
        return VERTICAL;
    }

    protected double valueAxisRotation() {
        return 0;
    }

}
