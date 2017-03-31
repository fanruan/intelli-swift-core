package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.general.FRLogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.taobao.top.link.embedded.websocket.util.StringUtil;

/**
 * Created by eason on 2017/2/27.
 */
public class VanDotWidget extends VanChartWidget{

    private static final int BUBBLE = 1;
    private static final int SCATTER = 2;

    private static final String TARGET = "50000";

    //气泡图和散点图的指标个数
    private static final int BUBBLE_COUNT = 3;
    private static final int SCATTER_COUNT = 2;

    private static final int BUBBLE_DIMENSION = 3;

    public JSONArray createSeries(JSONObject originData) throws Exception{

        JSONArray series = JSONArray.create();
        String[] ids = this.getUsedTargetID();

        if(!originData.has("t") || ids.length < SCATTER_COUNT){
            return series;
        }

        double xNumberScale = this.numberScale(ids[0]);
        double yNumberScale = this.numberScale(ids[1]);

        return series;
    }

    public String getSeriesType(String dimensionID){

        JSONObject scopes = this.getChartSetting().getScopes();

        int type = SCATTER;
        try {
            if(scopes.has(TARGET)){
                type = scopes.getJSONObject(TARGET).optInt("valueType", BUBBLE);
            }
        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage(), e);
        }

        int idCount = this.getUsedTargetID().length;

        return (idCount == BUBBLE_DIMENSION && type == BUBBLE ) ? "bubble" : "scatter";
    }
}
