package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.report.WidgetType;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.taobao.top.link.embedded.websocket.util.StringUtil;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCombinationWidget extends VanCartesianWidget{

    public JSONArray createSeries(JSONObject data) throws Exception{
        JSONArray series = super.createSeries(data);

        for(int i = 0, count = series.length(); i < count; i++){
            JSONObject ser = series.optJSONObject(i);
            String id = ser.optJSONArray("targetIDs").optString(0);
            VanCombineType type = this.getVanCombineType(id);

            ser.put("curve", VanCombineType.isCurve(type));
            ser.put("step", VanCombineType.isStep(type));
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
}
