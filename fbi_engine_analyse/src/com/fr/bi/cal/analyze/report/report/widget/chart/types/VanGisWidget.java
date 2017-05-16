package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

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

        JSONObject tooltip = plotOptions.optJSONObject("tooltip");
        tooltip.put("shared", true);

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();

        JSONArray children = originData.getJSONArray("c");
        for(int i = 0, len = targetIDs.length; i < len; i++){
            String id = targetIDs[i];
            double scale = this.numberScale(id);
            JSONArray data = JSONArray.create();
            for (int j = 0, count = children.length(); j < count; j++) {
                JSONObject lObj = children.getJSONObject(j);
                String lnglat = lObj.getString("n");
                String[] tmp = lnglat.split(",");

                if(tmp.length == 2 && StableUtils.string2Number(tmp[0]) != null){
                    JSONArray s = lObj.getJSONArray("s");
                    double value = s.isNull(i) ? 0 : s.getDouble(i);
                    JSONObject d = JSONObject.create().put("lnglat", tmp).put("value", value / scale);
                    JSONArray c = lObj.optJSONArray("c");
                    if(c != null && c.length() > 0){
                        d.put("name", c.optJSONObject(0).optString("n"));
                    }
                    data.put(d);
                }
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", this.getDimensionNameByID(id))
                    .put("targetIDs", JSONArray.create().put(id))
                    .put("dimensionIDs", dimensionIDs);            series.put(ser);
        }

        return series;
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE;
    }

    public String getSeriesType(String dimensionID){
        return "pointMap";
    }

    protected void toLegendJSON(JSONObject options, JSONObject settings) throws JSONException{
        options.put("legend", JSONObject.create().put("enabled", false));
        options.put("rangeLegend", JSONObject.create().put("enabled", false));
    }
}
