package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCombinationWidget extends VanCartesianWidget{

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

    private JSONObject getSeriesAccumulationItem(String seriesName){
        BIDimension seriesDim = this.getSeriesDimension();

        if(seriesDim != null && seriesDim.getChartSetting().hasSeriesAccumulation()){
            JSONArray items = seriesDim.getChartSetting().getSeriesAccumulation();
            for(int i = 0, count = items.length(); i < count; i++){
                JSONObject obj = items.optJSONObject(i);
                JSONArray objItems = obj.optJSONArray("items");
                for(int j = objItems.length() - 1; j >=0; j--){
                    if(ComparatorUtils.equals(objItems.optString(j), seriesName)){
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    public String getSeriesType(String dimensionID, String seriesName){

        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        if(item == null){
            return this.getSeriesType(dimensionID);
        }

        return item.optInt("type") == 1 ? "column" : "area";
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

        return item.optInt("type") == 4;

    }

    private boolean isStep(VanCombineType type, String seriesName){
        JSONObject item = this.getSeriesAccumulationItem(seriesName);

        if(item == null){
            return VanCombineType.isStep(type);
        }

        return item.optInt("type") == 5;
    }
}
