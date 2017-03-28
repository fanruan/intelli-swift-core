package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.core.SessionDealWith;

import java.awt.*;
import java.util.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by User on 2016/4/25.
 */
public abstract class VanChartWidget extends TableWidget {

    private static final int STYLE_GRADUAL = 2;

    //兼容前台用数字表示位置的写法，真xx丑
    private static final int TOP = 2;
    private static final int RIGHT = 3;
    private static final int BOTTOM = 4;
    private static final int LEFT = 5;

    //气泡图和散点图的指标个数
    private static final int BUBBLE_COUNT = 3;
    private static final int SCATTER_COUNT = 2;


    private HashMap<String, JSONArray> dimensionIdMap = new HashMap<String, JSONArray>();
    private HashMap<String, String> regionIdMap = new HashMap<String, String>();

    public abstract JSONArray createSeries(JSONObject data) throws JSONException;

    public abstract String getSeriesType(String dimensionID);

    protected boolean isStacked(String dimensionID){
        return false;
    }

    protected String getStackedKey(String dimensionID){
        return dimensionID;
    }

    protected int yAxisIndex(String dimensionID){
        return 0;
    }

    protected JSONArray getDimensionIDArray(String regionID){
        return dimensionIdMap.get(regionID);
    }

    protected String getRegionID(String dimensionID){
        return regionIdMap.get(dimensionID);
    }

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");

            JSONArray ja = JSONArray.create();
            Iterator it = vjo.keys();
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.parseInt(o1) - Integer.parseInt(o2);
                }
            });

            for(String region : sorted){

                if(ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
                        ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)){
                    continue;
                }

                JSONArray tmp =  vjo.getJSONArray(region);

                dimensionIdMap.put(region, tmp);//后面用来计算坐标轴和堆积属性

                for(int j = 0; j < tmp.length(); j++){
                    String key = tmp.getString(j);
                    ja.put(key);
                    regionIdMap.put(key, region);
                }

                vjo.remove(region);
            }

            vjo.put(BIReportConstant.REGION.TARGET1, ja);
        }

        super.parseJSON(jo, userId);
    }

    public JSONObject createPlotOptions(BISessionProvider session) throws Exception{

        JSONObject plotOptions = JSONObject.create();

        plotOptions.put("animation", true);

        //tooltip的默认配置
        JSONObject tooltip = JSONObject.create();
        JSONObject reportSetting = BIReadReportUtils.getInstance().getBIReportNodeJSON(((BISession) session).getReportNode());
        String widgetBg = reportSetting.optJSONObject("globalStyle").optJSONObject("widgetBackground").optString("value");

        tooltip.put("padding", 10).put("backgroundColor", widgetBg).put("borderRadius", 2).put("shadow", true)
                .put("style", JSONObject.create()
                        .put("color", this.isDarkColor(widgetBg) ? "#FFFFFF" : "#1A1A1A")
                        .put("fontSize", "14px").put("fontFamily", "Verdana"));

        return plotOptions;
    }

    private boolean isDarkColor(String colorStr){

        colorStr = colorStr.substring(1);

        Color color =  new Color(Integer.parseInt(colorStr, 16));

        return color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114 < 192;
    }

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = JSONObject.create();

        //图例
        settings.put("legend", BOTTOM)
                .put("legendStyle", this.defaultFont());

        return settings;
    }

    protected JSONObject defaultFont() throws JSONException{

        //todo 这边的字体要全局取一下
        return JSONObject.create()
                .put("fontFamily", "Microsoft YaHei")
                .put("color", "rgb(178, 178, 178)")
                .put("fontSize", "12px");

    }

    //todo 不知道有没有实现过，先撸一下
    private JSONObject merge(JSONObject target, JSONObject source) throws JSONException{
        Iterator it = source.keys();
        while(it.hasNext()){
            String key = it.next().toString();
            if(!target.has(key)){
                target.put(key, source.get(key));
            }
        }
        return target;
    }

    protected JSONObject getDetailChartSetting() throws JSONException{
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        return merge(settings, this.populateDefaultSettings());
    }

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {

        JSONObject data = super.createDataJSON(session).getJSONObject("data");

        JSONArray series = this.createSeries(data);

        return this.createOptions(session).put("series", series);
    }

    public JSONObject createOptions(BISessionProvider session) throws Exception{
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getDetailChartSetting();

        options.put("chartType", this.getSeriesType(StringUtils.EMPTY));

        if(settings.has("chartColor")){
            options.put("colors", settings.getJSONArray("chartColor"));
        }

        if(settings.optInt("chartStyle") == STYLE_GRADUAL){
            options.put("style", "gradual");
        }

        options.put("legend", this.parseLegend(settings));

        options.put("plotOptions", this.createPlotOptions(session));

        return options;
    }

    protected JSONArray createXYSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        if (originData.has("t")) {//有列表头，多系列
            JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
            JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");
            boolean isStacked = this.isStacked(targetIDs[0]);
            for (int i = 0; i < topC.length(); i++) {
                JSONObject tObj = topC.getJSONObject(i);
                String name = tObj.getString("n");
                JSONArray data = JSONArray.create();
                for (int j = 0; j < leftC.length(); j++) {
                    JSONObject lObj = leftC.getJSONObject(j);
                    String x = lObj.getString("n");
                    double y = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s").getDouble(0);
                    data.put(JSONObject.create().put("x", x).put("y", y));
                }
                JSONObject ser = JSONObject.create().put("data", data).put("name", name).put("type", this.getSeriesType(targetIDs[0]));
                if(isStacked){
                    ser.put("stacked", targetIDs[0]);
                }
                series.put(ser);
            }
        }else if(originData.has("c")){
            JSONArray children = originData.getJSONArray("c");
            for(int i = 0, len = targetIDs.length; i < len; i++){
                String id = targetIDs[i], type = this.getSeriesType(id), stackedKey = this.getStackedKey(id);
                JSONArray data = JSONArray.create();
                for (int j = 0, count = children.length(); j < count; j++) {
                    JSONObject lObj = children.getJSONObject(j);
                    String x = lObj.getString("n");
                    double y = lObj.getJSONArray("s").getDouble(i);
                    data.put(JSONObject.create().put("x", x).put("y", y));
                }
                JSONObject ser = JSONObject.create().put("data", data).put("name", id)
                        .put("type", type).put("yAxis", this.yAxisIndex(id));
                if(this.isStacked(id)){
                    ser.put("stacked", stackedKey);
                }
                series.put(ser);
            }
        }

        return series;
    }

    protected JSONArray createBubbleSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String type = this.getSeriesType(StringUtils.EMPTY);
        int targetCount = type == "bubble" ? BUBBLE_COUNT : SCATTER_COUNT;

        JSONArray children = originData.optJSONArray("c");

        for(int i = 0, len = children.length(); i < len; i++){

            JSONObject obj = children.optJSONObject(i);
            JSONArray data = obj.optJSONArray("s");
            int dataLen = data.length();

            if(dataLen < targetCount){
                continue;
            }

            double x = data.optDouble(0), y = data.optDouble(1), size = data.optDouble(2, y);

            series.put(
                JSONObject.create()
                        .put("type", type)
                        .put("name",  obj.optString("n"))
                        .put("data", JSONArray.create().put(JSONObject.create().put("x", x).put("y", y).put("size", size)))
            );
        }

        return series;
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException{

        int legend = settings.optInt("legend");
        String position = "top";

        if(legend == RIGHT){
            position = "right";
        }else if(legend == BOTTOM){
            position = "bottom";
        }else if(legend == LEFT){
            position = "left";
        }

        return JSONObject.create()
                .put("enabled", legend >= TOP)
                .put("position", position)
                .put("style", settings.optJSONObject("legendStyle"));
    }

    public BIDimension getCategoryDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public BIDimension getSeriesDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION2));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

}