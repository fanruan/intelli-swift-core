package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanGisWidget extends VanChartWidget{

    private static final String TILE_LAYER = "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    private static final String ATTRIBUTION = "<a><img src=\\\"http://webapi.amap.com/theme/v1.3/mapinfo_05.png\\\">&copy; 2016 AutoNavi</a>";
    private static final String GIS_ICON_PATH =  "?op=resource&resource=/com/fr/bi/web/images/icon/chartsetting/address_marker_big.png";

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = super.createOptions(globalStyle, data);

        options.put("geo", JSONObject.create().put("tileLayer", TILE_LAYER).put("attribution", ATTRIBUTION));

        return options;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        JSONObject icon = JSONObject.create().put("iconUrl", this.getRequestURL() + GIS_ICON_PATH).put("iconSize", new JSONArray("[24, 24]"));

        plotOptions.put("icon", icon);

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {

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
                data.put(JSONObject.create().put("lnglat", lnglat.split(",")).put("value", value));
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", this.getDimensionNameByID(id)).put("dimensionID", id);
            series.put(ser);
        }

        return series;
    }

    public String getSeriesType(String dimensionID){
        return "pointMap";
    }
}
