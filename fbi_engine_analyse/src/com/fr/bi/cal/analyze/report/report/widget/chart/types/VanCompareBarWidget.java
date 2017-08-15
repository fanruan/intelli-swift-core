package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.BaseUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareBarWidget extends VanCompareColumnWidget{

    protected int numberLevel(String dimensionID){
        int level = BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL;
        try {
            JSONObject settings = this.getDetailChartSetting();
            level = settings.optInt("leftYNumberLevel", level);
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return level;
    }


    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException {
        return super.parseLeftValueAxis(settings).put("position", "right");
    }

    public boolean isInverted(){
        return true;
    }

    protected double cateAxisRotation() {
        return VERTICAL;
    }

    protected double valueAxisRotation() {
        return 0;
    }

    //把值轴1的警戒线取负，把值轴2的警戒线放到值轴1里面"
    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{
        JSONArray axisArray = super.parseValueAxis(settings);
        JSONObject leftAxis = axisArray.optJSONObject(0);
        if(leftAxis != null){
            JSONArray resultLines = JSONArray.create();
            JSONObject rightAxis = axisArray.optJSONObject(1);
            if(rightAxis != null && rightAxis.has("plotLines")){
                resultLines = rightAxis.optJSONArray("plotLines");
            }
            JSONArray plotLines = leftAxis.optJSONArray("plotLines");
            if(plotLines != null){
                for(int i = 0, len = plotLines.length(); i < len;i++){
                    JSONObject line = plotLines.optJSONObject(i);
                    line.put("value", -line.optDouble("value"));
                    resultLines.put(line);
                }
            }
            leftAxis.put("plotLines", resultLines);
        }

        return axisArray;
    }

    protected void dealCompareChartYAxis(JSONObject settings) throws JSONException {
    }

    protected JSONArray dealSeriesWithEmptyAxis(JSONArray series) throws JSONException{
        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);

            int yAxisIndex = ser.optInt("yAxis");
            if(yAxisIndex == 0){
                ser.put("xAxis", 1);
                JSONArray datas = ser.optJSONArray("data");
                String valueKey = this.valueKey();
                for (int j = 0, size = datas.length(); j < size; j++) {
                    JSONObject point = datas.getJSONObject(j);
                    if(StableUtils.isNumber(point.optString(valueKey))){
                        point.put(valueKey, -checkInfinity(point.optDouble(valueKey)));
                    }
                }
            }

            ser.put("yAxis", 0);
        }

        return series;
    }

    protected String valueFormatFunc(BISummaryTarget dimension, boolean isTooltip) {

        int index = yAxisIndex(dimension.getValue());

        String format = this.valueFormat(dimension);
        String unit = this.valueUnit(dimension, isTooltip);

        return index == 0 ? String.format("function(){return BI.contentFormat(-arguments[0], \"%s\") + \"%s\"}", format, unit)
                : String.format("function(){return BI.contentFormat(arguments[0], \"%s\") + \"%s\"}", format, unit);
    }

    protected String labelString(int yAxis){
        return "Math.abs(arguments[0])";
    }

}
