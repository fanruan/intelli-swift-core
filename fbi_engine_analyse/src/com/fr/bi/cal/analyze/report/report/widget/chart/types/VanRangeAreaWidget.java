package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanRangeAreaWidget extends VanAreaWidget{

    public JSONArray createSeries(JSONObject originData) throws JSONException {

        return this.createStackedEmptySeries(originData);

    }

}
