package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/2/27.
 */
public class VanTreeMapWidget extends VanChartWidget{

    public JSONArray createSeries(JSONObject originData) throws JSONException {

        JSONArray series = JSONArray.create();

        if(!originData.has("t")){
            return series;
        }

        JSONArray data = JSONArray.create();
        JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
        JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");

        for (int i = 0; i < topC.length(); i++) {
            JSONArray children = JSONArray.create();

            JSONObject tObj = topC.getJSONObject(i);
            double sum = 0;
            for (int j = 0; j < leftC.length(); j++) {
                JSONObject lObj = leftC.getJSONObject(j);
                String name = lObj.getString("n");
                double value = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s").getDouble(0);

                sum += value;

                children.put(JSONObject.create().put("name", name).put("value", value));
            }

            data.put(JSONObject.create().put("name", tObj.getString("n")).put("value", sum).put("children", children));
        }

        return series.put(JSONObject.create().put("data", data).put("name", StringUtils.EMPTY));
    }

    public String getSeriesType(String dimensionID){
        return "treeMap";
    }
}
