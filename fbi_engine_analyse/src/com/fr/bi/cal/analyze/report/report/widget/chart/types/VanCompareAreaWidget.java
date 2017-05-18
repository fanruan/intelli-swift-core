package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/20.
 */
public class VanCompareAreaWidget extends VanAreaWidget{

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException {

        dealCompareChartYAxis(settings);

        return super.parseValueAxis(settings);
    }

    @Override
    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException {
        JSONArray array = super.parseCategoryAxis(settings);
        array.put(createEmptyCategoryAxis(settings));
        return array;
    }

    @Override
    public JSONArray createSeries(JSONObject data) throws Exception {
        JSONArray series = super.createSeries(data);
        return dealSeriesWithEmptyAxis(series);
    }

}
