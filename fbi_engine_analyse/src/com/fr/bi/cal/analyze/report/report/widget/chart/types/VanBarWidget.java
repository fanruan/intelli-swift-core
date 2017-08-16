package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanBarWidget extends VanColumnWidget{

    public boolean isInverted(){
        return true;
    }

    protected double cateAxisRotation() {
        return VERTICAL;
    }

    protected double valueAxisRotation() {
        return 0;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException {
        return super.parseLeftValueAxis(settings).put("position", "right").put("reversed", false);
    }

    @Override
    protected boolean hasDataSheet(JSONObject settings) {
        return false;
    }
}
