package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/3/20.
 */
public class VanPercentStackedColumnWidget extends VanStackedColumnWidget{

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        //图例
        settings.put("leftYNumberLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT);

        return settings;
    }

    public JSONArray createSeries(JSONObject data) throws Exception {
        JSONArray series =  createXYSeries(data);

        for(int i = 0, len = series.length(); i < len; i++){
            series.getJSONObject(i).put("stackByPercent", true);
        }

        return series;
    }

    protected double numberScale(String dimensionID) {
        return 1;
    }

    protected String scaleUnit(int level) {
        return StringUtils.EMPTY;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException{
        JSONObject left = super.parseLeftValueAxis(settings);

        left.put("formatter", "function(){return this * 100}");

        return left;
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE + PERCENT;
    }
}
