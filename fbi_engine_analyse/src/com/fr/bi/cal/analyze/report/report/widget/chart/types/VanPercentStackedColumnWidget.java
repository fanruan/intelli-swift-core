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

    //数据点提示和标签不带百分号
    protected String scaleUnit(int level) {
        return StringUtils.EMPTY;
    }

    //值轴标题带百分号
    protected String axisTitleUnit(int level, String unit){
        String result = PERCENT_SYMBOL;
        result += unit;
        return StringUtils.isEmpty(result) ? StringUtils.EMPTY : "(" + result + ")";
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException{
        JSONObject left = super.parseLeftValueAxis(settings);

        left.put("reversed", false);
        left.put("formatter", "function(){return this * 100}");

        return left;
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE + PERCENT;
    }
}
