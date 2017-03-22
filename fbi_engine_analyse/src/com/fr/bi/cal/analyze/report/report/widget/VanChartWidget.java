package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.conf.session.BISessionProvider;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
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

    public JSONObject createPlotOptions() throws JSONException{
        return JSONObject.create();
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

    protected JSONObject getDetailChartSetting() throws JSONException{
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        return settings.length() == 0 ? this.populateDefaultSettings() : settings;
    }

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {

        JSONObject data = super.createDataJSON(session).getJSONObject("data");

        JSONArray series = this.createSeries(data);

        return this.createOptions().put("series", series);
    }

    public JSONObject createOptions() throws JSONException{
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getDetailChartSetting();

        if(settings.has("chartColor")){
            options.put("colors", settings.getJSONArray("chartColor"));
        }

        if(settings.optInt("chartStyle") == STYLE_GRADUAL){
            options.put("style", "gradual");
        }

        options.put("legend", this.parseLegend(settings));

        options.put("plotOptions", this.createPlotOptions());

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

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

}