package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanPieWidget extends VanChartWidget{

    private static final int NORMAL = 1;      //普通形态
    private static final int EQUAL_ARC_ROSE = 4; //等弧玫瑰图
    private static final int NOT_EQUAL_ARC_ROSE = 5; //不等弧玫瑰图

    private static final int CIRCLE = 360;

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        settings.put("pieChartType", NORMAL);

        settings.put("innerRadius", 0);

        settings.put("totalAngle", CIRCLE);

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        int type = settings.optInt("pieChartType");

        plotOptions.put("innerRadius", settings.optInt("innerRadius") + "%");
        plotOptions.put("endAngle", settings.optInt("totalAngle"));
        plotOptions.put("roseType", type == NORMAL ? StringUtils.EMPTY : (type == EQUAL_ARC_ROSE ? "sameArc":"differentArc"));

        return plotOptions;
    }

    public String getSeriesType(String dimensionID){
        return "pie";
    }

    protected String getTooltipIdentifier(){
        return CATEGORY + SERIES + VALUE + PERCENT;
    }

    protected String categoryLabelKey() {
        return SERIES;
    }

    protected String seriesLabelKey() {
        return CATEGORY;
    }

    protected JSONArray createSeriesWithChildren(JSONObject originData) throws Exception {
        BIDimension category = this.getCategoryDimension();
        JSONArray children = originData.optJSONArray("c");

        if(category == null && children == null){//没有分类，只有指标，显示一个饼
            String[] targetIDs = this.getUsedTargetID();
            JSONArray datas = JSONArray.create();
            JSONArray targets = JSONArray.create();
            for (int i = 0, len = targetIDs.length; i < len; i++) {
                String id = targetIDs[i];
                JSONArray targetValues = originData.optJSONArray("s");
                double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale(id);
                datas.put(JSONObject.create().put("y", numberFormat(id, y)).put("x", getDimensionNameByID(id)));
                targets.put(id);
            }

            JSONObject ser = JSONObject.create().put("data", datas).put("type", "pie").put("dimensionIDs", JSONArray.create()).put("targetIDs", targets);

            return JSONArray.create().put(ser);

        }
        return super.createSeriesWithChildren(originData);
    }

    protected JSONObject defaultDataLabelSetting() throws JSONException {

        return JSONObject.create().put("showCategoryName", false)
                .put("showSeriesName", false).put("showValue", true).put("showPercentage", true)
                .put("position", BIChartSettingConstant.DATA_LABEL.POSITION_OUTER).put("showTractionLine", true)
                .put("textStyle", defaultFont());

    }

}
