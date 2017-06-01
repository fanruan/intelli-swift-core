package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareColumnWidget extends VanColumnWidget{

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        dealCompareChartYAxis(settings);

        return super.parseValueAxis(settings);

    }

    //这个暂时只是为了对比条形图极简模式下，第二个分类轴标签不显示
    protected boolean showLabelInMiniMode(boolean cate, int index) {
        return cate && index == 0;
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
