package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/3/20.
 */
public class VanGaugeWidget extends VanCartesianWidget{

    private static final int NORMAL = 1;     //360的仪表盘
    private static final int HALF_DASHBOARD = 9;//180'的仪表盘
    private static final int PERCENT_DASHBOARD = 10;//百分比的仪表盘
    private static final int PERCENT_SCALE_SLOT = 11;//带刻度槽的仪表盘
    private static final int VERTICAL_TUBE = 12;     //竖起来的试管型仪表盘
    private static final int HORIZONTAL_TUBE = 13;//横过来的试管型仪表盘

    private static final int SINGLE_POINTER = 1;
    private static final int MULTI_POINTERS = 2;

    private static final String BG_COLOR = "rgb(245,245,247)";


    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        settings.put("dashboardChartType", NORMAL);

        settings.put("dashboardPointer", SINGLE_POINTER);

        settings.put("styleRadio", AUTO);
        settings.put("dashboardStyles", JSONArray.create());

        settings.put("minScale", StringUtils.EMPTY);
        settings.put("maxScale", StringUtils.EMPTY);

        return settings;
    }

    protected String getCoordYKey(){
        return "gaugeAxis";
    }

    //仪表盘的标签样式固定
    public JSONObject createPlotOptions(JSONObject globalSetting, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalSetting, settings);

        int gaugeStyle = settings.optInt("dashboardChartType");
        boolean isPointer = gaugeStyle == NORMAL || gaugeStyle == HALF_DASHBOARD;

        String align = gaugeStyle == HORIZONTAL_TUBE ? "top" : "left";

        String valueLabelKey = (isPointer ? SERIES : CATEGORY) + VALUE;
        JSONObject formatter = JSONObject.create().put("identifier", valueLabelKey);

        JSONObject valueLabel = JSONObject.create().put("enabled", true).put("backgroundColor", BG_COLOR).put("align", align).put("formatter", formatter);

        JSONObject seriesLabel = JSONObject.create().put("enabled", true).put("formatter", JSONObject.create().put("identifier", CATEGORY)).put("align", "bottom");

        JSONObject percentageLabel = JSONObject.create().put("enabled", true).put("formatter", JSONObject.create().put("identifier", PERCENT).put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}")).put("align", align);

        plotOptions.put("valueLabel", valueLabel).put("seriesLabel", seriesLabel).put("percentageLabel", percentageLabel);

        return plotOptions;
    }

    protected String dataLabelsKey() {
        return "valueLabel";
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
        boolean multi = gaugeStyle == NORMAL || gaugeStyle == HALF_DASHBOARD;
        multi = multi && pointerCount == MULTI_POINTERS;

        JSONArray newSeries = JSONArray.create();
        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);
            ser.put("style", style).put("thermometerLayout", layout);

            if(multi){
                JSONObject combineSer = newSeries.optJSONObject(0);
                if(combineSer == null){
                    combineSer = ser;
                    newSeries.put(combineSer);
                } else {//将多个指标的点放到一个系列里
                    JSONArray combineData = combineSer.optJSONArray("data");
                    JSONArray datas = ser.optJSONArray("data");
                    for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex ++) {
                        combineData.put(datas.opt(dataIndex));
                    }
                    combineSer.put("data", combineData);
                }
            } else {//将一个系列的多个点拆成多个系列
                JSONArray datas = ser.optJSONArray("data");
                for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex ++){
                    JSONObject newSer = new JSONObject(ser.toString());
                    newSer.put("data", JSONArray.create().put(datas.optJSONObject(dataIndex)));
                    newSeries.put(newSer);
                }
            }

        }

        return newSeries;
    }

    protected JSONObject createDataLabels(JSONObject settings) throws JSONException{
        return JSONObject.EMPTY;
    }

    public String getSeriesType(String dimensionID){
        return "gauge";
    }
}
