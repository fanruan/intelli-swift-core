package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.report.WidgetType;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCombinationWidget extends VanCartesianWidget{

    public String getSeriesType(String dimensionID){

        JSONObject scopes = this.getChartSetting().getScopes();

        String regionID = this.getRegionID(dimensionID);

        try {
            if(scopes.has(regionID)){
                int chartType = scopes.getJSONObject(regionID).optInt("chartType");
                return VanCombineType.parseStringType(chartType);
            }
        }catch (Exception e){
            BILoggerFactory.getLogger().info(e.getMessage());
        }

        return "column";
    }

    public boolean isStacked(String dimensionID){
        return StringUtils.isNotBlank(this.getStackedKey(dimensionID));
    }


    public String getStackedKey(String dimensionID){

        String regionID = this.getRegionID(dimensionID);

        JSONArray dIDs = this.getDimensionIDArray(regionID);

        return dIDs != null && dIDs.length() > 1 ? dIDs.optString(0) : StringUtils.EMPTY;
    }
}
