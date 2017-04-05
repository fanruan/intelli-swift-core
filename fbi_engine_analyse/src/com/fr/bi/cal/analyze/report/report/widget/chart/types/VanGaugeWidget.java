package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.field.target.target.BINumberTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/3/20.
 */
public class VanGaugeWidget extends VanChartWidget{

    private static final int NORMAL = 1;     //360的仪表盘
    private static final int HALF_DASHBOARD = 9;//180'的仪表盘
    private static final int PERCENT_DASHBOARD = 10;//百分比的仪表盘
    private static final int PERCENT_SCALE_SLOT = 11;//带刻度槽的仪表盘
    private static final int VERTICAL_TUBE = 12;     //竖起来的试管型仪表盘
    private static final int HORIZONTAL_TUBE = 13;//横过来的试管型仪表盘

    private static final int SINGLE_POINTER = 1;
    private static final int MULTI_POINTERS = 2;


    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        settings.put("dashboardChartType", NORMAL);

        settings.put("dashboardPointer", SINGLE_POINTER);

        settings.put("styleRadio", AUTO);
        settings.put("dashboardStyles", JSONArray.create());

        settings.put("minScale", StringUtils.EMPTY);
        settings.put("maxScale", StringUtils.EMPTY);

        settings.put("showPercentage", false);

        return settings;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception{

        JSONArray series = this.createXYSeries(originData);

        JSONObject settings = this.getDetailChartSetting();
        int gaugeStyle = settings.optInt("dashboardChartType");
        String style = StringUtils.EMPTY, layout = StringUtils.EMPTY;
        if(gaugeStyle == NORMAL){
            style = "pointer";
        }else if(gaugeStyle == HALF_DASHBOARD){
            style = "pointer_semi";
        }else if(gaugeStyle == PERCENT_DASHBOARD){
            style = "ring";
        }else if(gaugeStyle == PERCENT_SCALE_SLOT){
            style = "slot";
        }else if(gaugeStyle == VERTICAL_TUBE){
            style = "thermometer";
            layout = "vertical";
        }else if(gaugeStyle == HORIZONTAL_TUBE){
            style = "thermometer";
            layout = "horizontal";
        }

        int pointerCount = settings.optInt("dashboardPointer");

        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);
            ser.put("style", style).put("thermometerLayout", layout);

            if(pointerCount == SINGLE_POINTER){
                JSONArray data = ser.optJSONArray("data");
                ser.put("data", JSONArray.create().put(data.getJSONObject(0)));
            }
        }

        return series;
    }

    protected int numberLevel(String dimensionID){
        return BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL;
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception{

    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception{

    }

    public String getSeriesType(String dimensionID){
        return "gauge";
    }
}
