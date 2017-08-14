package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.report.map.BIMapInfoManager;
import com.fr.bi.conf.report.map.BIWMSManager;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.Map;

/**
 * Created by eason on 2017/2/27.
 */
public class VanMapWidget extends VanChartWidget{

    private String subType = StringUtils.EMPTY;

    private static final String THEME = "#04b1c2";

    public void parseJSON(JSONObject jo, long userId) throws Exception {

        super.parseJSON(jo, userId);

        if(jo.has("subType")){
            subType = jo.optString("subType");
        }
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = super.createOptions(globalStyle, data);

        BIMapInfoManager manager = BIMapInfoManager.getInstance();

        if(StringUtils.isBlank(this.subType)){
            for (Map.Entry<String, Integer> entry : manager.getinnerMapLayer().entrySet()) {
                if(entry.getValue() == 0){
                    subType = entry.getKey();
                    break;
                }
            }
        }

        String d = manager.getinnerMapPath().get(subType);
        String n = manager.getinnerMapTypeName().get(subType);

        if(d == null){
            d = manager.getCustomMapPath().get(subType);
        }

        if(n == null){
            n = manager.getCustomMapTypeName().get(subType);
        }

        JSONObject geo = JSONObject.create().put("data", d).put("name", n).put("zoom", true);

        JSONObject settings = this.getDetailChartSetting();

        if(settings.optBoolean("isShowBackgroundLayer") && settings.has("backgroundLayerInfo")){
            JSONObject config = BIWMSManager.getInstance().getWMSInfo(settings.optString("backgroundLayerInfo"));
            geo.put("tileLayer", config.optString("url"));
        }

        options.put("geo", geo);

        options.put("dTools", JSONObject.create()
                .put("enabled", true).put("currentColor", "62b2ef").put("backgroundColor", "white")
                .put("style", JSONObject.create().put("fontFamily", "Microsoft YaHei").put("color", "#b2b2b2").put("fontSize", "12px"))
        );

        return options;
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException{

        JSONObject legend = super.parseLegend(settings);

        JSONArray mapStyle;
        if(settings.optInt("styleRadio", AUTO) == AUTO){
            legend.put("range", JSONObject.create().put("color", settings.optString("themeColor")));
        }else{
            mapStyle = settings.optJSONArray("mapStyles");
            legend.put("range", this.mapStyleToRange(mapStyle));
        }

        legend.put("continuous", false);

        BISummaryTarget[] targets = this.getTargets();
        if(targets.length > 0){
            legend.put("formatter", this.intervalLegendFormatter(this.valueFormat(targets[0]), this.valueUnit(targets[0], true)));
        }

        return legend;
    }

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("isShowBackgroundLayer", true);

        settings.put("backgroundLayerInfo", Inter.getLocText("BI-GAO_DE_MAP"));

        settings.put("styleRadio", AUTO);

        settings.put("themeColor", THEME);

        //todo 自动的时候应该可以删掉
        settings.put("mapStyle", JSONArray.create());

        return settings;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {
        JSONArray series = JSONArray.create();
        String[] dimensionIDs = this.getUsedDimensionID();
        if(!originData.has("c")){
            return series;
        }

        String[] targetIDs = this.getUsedTargetID();
        BIMapInfoManager manager = BIMapInfoManager.getInstance();

        for(int i = 0, seriesCount = targetIDs.length; i < seriesCount; i++){
            String id = targetIDs[i];

            boolean isBubble = ComparatorUtils.equals(this.getRegionID(id), BIReportConstant.REGION.TARGET2);
            String key = isBubble ? "size" : "value";
            String type = isBubble ? "bubble" : "areaMap";

            JSONArray data = JSONArray.create(), rawData = originData.getJSONArray("c");

            double scale = this.numberScale(id);
            for(int j = 0, dataCount = rawData.length(); j < dataCount; j++){
                JSONObject item = rawData.getJSONObject(j);
                JSONArray s = item.getJSONArray("s");
                double value = s.isNull(i) ? 0 : s.getDouble(i);
                String areaName =  item.optString("n");

                JSONObject datum = JSONObject.create().put("name", areaName).put(key, numberFormat(id,value / scale));

                if(item.has("c") && manager.getinnerMapName().containsKey(areaName)){
                    JSONObject drillDown = JSONObject.create();
                    String mapPath = manager.getinnerMapName().get(areaName);
                    drillDown.put("series", this.createSeries(item));
                    drillDown.put("geo", JSONObject.create().put("data", manager.getinnerMapPath().get(mapPath)).put("name", areaName));
                    datum.put("drilldown", drillDown);
                }

                data.put(datum);
            }

            series.put(JSONObject.create().put("data", data).put("type", type)
                    .put("name", this.getDimensionNameByID(id))
                    .put("targetIDs", JSONArray.create().put(id))
                    .put("dimensionIDs", dimensionIDs));
        }

        return series;
    }

    public String getSeriesType(String dimensionID){
        return "areaMap";
    }

    protected String getLegendType(JSONObject settings){
        return "rangeLegend";
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE + SIZE;
    }

    protected JSONArray parseColors(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws Exception {
        return JSONArray.create();
    }

    protected JSONObject defaultDataLabelSetting() throws JSONException {

        return JSONObject.create().put("showBlockName", false).put("showTargetName", false)
                .put("showValue", true).put("showPercentage", false)
                .put("textStyle", defaultFont());

    }

    //地图因为gis背景，不自适应颜色
    protected JSONObject defaultFont() throws JSONException {
        return JSONObject.create().put("fontFamily", "Microsoft YaHei").put("fontSize", "12px").put("color", "#666666");
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }

}
