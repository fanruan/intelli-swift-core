package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCombinationWidget extends VanCartesianWidget{

    private static final int STACK_COLUMN = 1;
    private static final int STACK_AREA_CURVE = 4;
    private static final int STACK_AREA_STEP = 5;

    public JSONObject createPlotOptions(JSONObject globalSetting, JSONObject settings) throws Exception {
        return super.createPlotOptions(globalSetting, settings).put("marker", JSONObject.create().put("symbol", "circle").put("enabled", true).put("radius", 4.5));
    }

    public JSONArray createSeries(JSONObject data) throws Exception{
        JSONArray series = super.createSeries(data);

        for(int i = 0, count = series.length(); i < count; i++){
            JSONObject ser = series.optJSONObject(i);
            String id = ser.optJSONArray("targetIDs").optString(0);
            String name = ser.optString("name");
            VanCombineType type = this.getVanCombineType(id);
            ser.put("curve", this.isCurve(type, name));
            ser.put("step", this.isStep(type, name));
        }

        return series;
    }

    private VanCombineType getVanCombineType(String dimensionID){

        JSONObject scopes = this.getChartSetting().getScopes();

        String regionID = this.getRegionID(dimensionID);

        try {
            if(scopes.has(regionID)){
                int chartType = scopes.getJSONObject(regionID).optInt("chartType");
                return VanCombineType.parse(chartType);
            }
        }catch (Exception e){
            BILoggerFactory.getLogger().info(e.getMessage());
        }

        return VanCombineType.COLUMN;

    }

    public String getSeriesType(String dimensionID){
        return VanCombineType.parseStringType(this.getVanCombineType(dimensionID));
    }

    public boolean isStacked(String dimensionID){
        return VanCombineType.isStacked(this.getVanCombineType(dimensionID));
    }

    public String getStackedKey(String dimensionID){

        String regionID = this.getRegionID(dimensionID);

        return this.isStacked(dimensionID) ? regionID : StringUtils.EMPTY;
    }

    public String getSeriesType(String dimensionID, String seriesName){

        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        if(item == null){
            return this.getSeriesType(dimensionID);
        }

        return item.optInt("type") == STACK_COLUMN ? "column" : "area";
    }

    public boolean isStacked(String dimensionID, String seriesName){
        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        return item == null ? this.isStacked(dimensionID) : true;
    }

    public String getStackedKey(String dimensionID, String seriesName){

        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        return item == null ?  this.getStackedKey(dimensionID) : item.optString("index");
    }

    private boolean isCurve(VanCombineType type, String seriesName){


        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        if(item == null){
            return VanCombineType.isCurve(type);
        }

        return item.optInt("type") == STACK_AREA_CURVE;

    }

    private boolean isStep(VanCombineType type, String seriesName){
        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        if(item == null){
            return VanCombineType.isStep(type);
        }

        return item.optInt("type") == STACK_AREA_STEP;
    }

    @Override
    public boolean canCompleteMissTime(){
        return true;
    }
}
