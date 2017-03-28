package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanGisWidget extends VanChartWidget{

    private static final String TILE_LAYER = "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    private static final String ATTRIBUTION = "<a><img src=\\\"http://webapi.amap.com/theme/v1.3/mapinfo_05.png\\\">&copy; 2016 AutoNavi</a>";

    public JSONObject createOptions() throws JSONException{
        JSONObject options = super.createOptions();

        options.put("geo", JSONObject.create().put("tileLayer", TILE_LAYER).put("attribution", ATTRIBUTION));

        return options;
    }

    public JSONArray createSeries(JSONObject originData) throws JSONException {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();

        JSONArray children = originData.getJSONArray("c");
        for(int i = 0, len = targetIDs.length; i < len; i++){
            String id = targetIDs[i];
            JSONArray data = JSONArray.create();
            for (int j = 0, count = children.length(); j < count; j++) {
                JSONObject lObj = children.getJSONObject(j);
                String lnglat = lObj.getString("n");
                double value = lObj.getJSONArray("s").getDouble(i);
                data.put(JSONObject.create().put("lnglat", lnglat).put("value", value));
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", id);
            series.put(ser);
        }

        return JSONArray.create();
    }

    public String getSeriesType(String dimensionID){
        return "pointMap";
    }
}
