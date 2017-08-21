package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.report.map.BIWMSManager;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;

import java.util.List;

/**
 * Created by eason on 2017/2/27.
 */
public class VanGisWidget extends VanChartWidget{

    private static final String TILE_LAYER = "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    private static final String ATTRIBUTION = "<a><img src=\"http://webapi.amap.com/theme/v1.3/mapinfo_05.png\">&copy; 2016 AutoNavi</a>";
    private static final String GIS_ICON_PATH =  "?op=resource&resource=/com/fr/bi/web/images/1x/icon/chartsetting/address_marker_big.png";

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("isShowBackgroundLayer", true);

        settings.put("backgroundLayerInfo", Inter.getLocText("BI-GAO_DE_MAP"));

        return settings;
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = super.createOptions(globalStyle, data);

        JSONObject settings = this.getDetailChartSetting();

        JSONObject geo = JSONObject.create();
        if(settings.optBoolean("isShowBackgroundLayer")){
            JSONObject config = BIWMSManager.getInstance().getWMSInfo(settings.optString("backgroundLayerInfo"));
            geo.put("tileLayer", config.optString("url"));
        }
        options.put("geo", geo);

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

    private boolean latFirst() {
        BIDimension dimension = getCategoryDimension();
        JSONObject posJSON = dimension.getChartSetting().getPosition();
        if(posJSON != null){
            return posJSON.optInt("position") != 4;
        }
        return false;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();
        boolean latFirst = latFirst();//纬度在前

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
                    if(latFirst) {
                        String lat = tmp[1];
                        tmp[1] = tmp[0];
                        tmp[0] = lat;
                    }
                    JSONArray s = lObj.getJSONArray("s");
                    double value = s.isNull(i) ? 0 : s.getDouble(i);
                    JSONObject d = JSONObject.create().put("lnglat", tmp).put("value", numberFormat(id,value / scale));
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

    protected void dealView(List<String> sorted, JSONObject vjo) throws JSONException{
        super.dealView(sorted, vjo);

        JSONArray ja = JSONArray.create();

        int seriesRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION2);

        for (String region : sorted) {

            if (Integer.parseInt(region) > seriesRegion) {
                continue;
            }

            JSONArray tmp = vjo.getJSONArray(region);

            for (int j = 0; j < tmp.length(); j++) {
                String key = tmp.getString(j);
                ja.put(key);
            }

            vjo.remove(region);
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, ja);
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

    //地图因为gis背景，不自适应颜色
    protected JSONObject defaultFont() throws JSONException {
        return JSONObject.create().put("fontFamily", "Microsoft YaHei").put("fontSize", "12px").put("color", "#666666");
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }
}
