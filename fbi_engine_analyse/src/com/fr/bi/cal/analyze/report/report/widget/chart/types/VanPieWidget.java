package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
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

        plotOptions.put("innerRadius", settings.optInt("innerRadius"));
        plotOptions.put("endAngle", settings.optInt("totalAngle"));
        plotOptions.put("roseType", type == NORMAL ? StringUtils.EMPTY : (type == EQUAL_ARC_ROSE ? "sameArc":"differentArc"));

        return plotOptions;
    }

    public String getSeriesType(String dimensionID){
        return "pie";
    }

    protected String getTooltipIdentifier(){
        return SERIES + VALUE;
    }
}
