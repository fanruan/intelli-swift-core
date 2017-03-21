package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.conf.session.BISessionProvider;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.third.org.apache.poi.util.StringUtil;
import com.fr.web.core.SessionDealWith;

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

    public abstract JSONArray createSeries(JSONObject data) throws JSONException;

    public abstract String getSeriesType();

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {

        JSONObject data = super.createDataJSON(session).getJSONObject("data");

        JSONArray series = this.createSeries(data);

        return this.createOptions().put("series", series);
    }

    public JSONObject createOptions() throws JSONException{
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        if(settings.has("chartColor")){
            options.put("colors", settings.getJSONArray("chartColor"));
        }

        if(settings.optInt("chartStyle") == STYLE_GRADUAL){
            options.put("style", "gradual");
        }

        options.put("legend", this.parseLegend(settings));

        return options;
    }

    protected JSONArray createXYSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String type = this.getSeriesType();
        if (originData.has("t")) {//有列表头，多系列
            JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
            JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");
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
                series.put(JSONObject.create().put("data", data).put("name", name).put("type", type));
            }
        }else if(originData.has("c")){//单系列
            JSONArray children = originData.getJSONArray("c");
            JSONArray data = JSONArray.create();
            for (int j = 0; j < children.length(); j++) {
                JSONObject lObj = children.getJSONObject(j);
                String x = lObj.getString("n");
                double y = lObj.getJSONArray("s").getDouble(0);
                data.put(JSONObject.create().put("x", x).put("y", y));
            }
            series.put(JSONObject.create().put("data", data).put("name", StringUtils.EMPTY).put("type", type));
        }

        return series;
    }

    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException{

        JSONObject category = JSONObject.create();

        category
                .put("type", "category")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("catShowTitle")).put("style", this.checkTextStyle(settings.optJSONObject("catTitleStyle"))).put("text", settings.optString("catTitle")))
                .put("showLabel", settings.optBoolean("catShowLabel"))
                .put("labelStyle", this.checkTextStyle(settings.optJSONObject("catLabelStyle")))
                .put("lineColor", settings.optString("catLineColor"));

        return JSONArray.create().put(category);
    }

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        JSONArray axis = JSONArray.create();
        JSONObject labelStyle = settings.optJSONObject("leftYLabelStyle");

        JSONObject left = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("leftYShowTitle")).put("style", this.checkTextStyle(settings.optJSONObject("leftYTitleStyle"))).put("text", settings.optString("leftYTitle")))
                .put("showLabel", settings.optBoolean("leftYShowLabel"))
                .put("labelStyle", this.checkTextStyle(labelStyle.optJSONObject("textStyle")))
                .put("lineColor", settings.optString("leftYLineColor"));

        labelStyle = settings.optJSONObject("rightYLabelStyle");
        JSONObject right = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("rightYShowTitle")).put("style", this.checkTextStyle(settings.optJSONObject("rightYTitleStyle"))).put("text", settings.optString("rightYTitle")))
                .put("showLabel", settings.optBoolean("rightYShowLabel"))
                .put("labelStyle", this.checkTextStyle(labelStyle.optJSONObject("textStyle")))
                .put("lineColor", settings.optString("rightYLineColor"));

        axis.put(left);
        axis.put(right);

        if(settings.has("rightY2LineColor")){
            labelStyle = settings.optJSONObject("rightY2LabelStyle");
            JSONObject right2 = JSONObject.create()
                    .put("type", "value")
                    .put("title", JSONObject.create().put("enabled", settings.optJSONObject("rightY2ShowTitle")).put("style", this.checkTextStyle(settings.optJSONObject("rightY2TitleStyle"))).put("text", settings.optString("rightY2Title")))
                    .put("showLabel", settings.optBoolean("rightY2ShowLabel"))
                    .put("labelStyle", this.checkTextStyle(labelStyle.optJSONObject("textStyle")))
                    .put("lineColor", settings.optString("rightY2LineColor"));

            axis.put(right2);
        }

        return axis;
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

        return JSONObject.create().put("enabled", legend >= TOP).put("position", position).put("style", this.checkTextStyle(settings.optJSONObject("legendStyle")));
    }

    //过来的setting里，fontSize没有单位
    protected JSONObject checkTextStyle(JSONObject textStyle) throws JSONException{
        if(textStyle != null && textStyle.has("fontSize")){
            return new JSONObject(textStyle.toString()).put("fontSize", textStyle.optInt("fontSize") + "px");
        }
        return textStyle;
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

}