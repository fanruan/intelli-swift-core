package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.conf.session.BISessionProvider;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;

/**
 * Created by User on 2016/4/25.
 */
public abstract class VanChartWidget extends TableWidget {

    private static final int STYLE_GRADULA = 2;

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {

        JSONObject data = super.createDataJSON(session).getJSONObject("data");

        JSONArray series = this.createSeries(data);

        return this.createOptions().put("series", series);
    }


    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }


    public abstract JSONArray createSeries(JSONObject data) throws JSONException;

    public JSONObject createOptions() throws JSONException{
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        if(settings.has("chartColor")){
            options.put("colors", settings.getJSONArray("chartColor"));
        }

        if(settings.getInt("chartStyle") == STYLE_GRADULA){
            options.put("style", "gradual");
        }


        return options;
    }

    public String getSeriesType(){
        return "column";
    }

    protected JSONArray createXYSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String type = this.getSeriesType();
        if (originData.has("t")) {
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
        }
        return series;
    }

}