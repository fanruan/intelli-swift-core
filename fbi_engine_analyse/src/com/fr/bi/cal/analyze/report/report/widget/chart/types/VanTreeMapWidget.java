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

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();

        if(!originData.has("t") || targetIDs.length == 0){
            return series;
        }

        JSONArray data = JSONArray.create();
        JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
        JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");

        double scale = this.numberScale(targetIDs[0]);
        for (int i = 0; i < topC.length(); i++) {
            JSONArray children = JSONArray.create();

            JSONObject tObj = topC.getJSONObject(i);
            double sum = 0;
            for (int j = 0; j < leftC.length(); j++) {
                JSONObject lObj = leftC.getJSONObject(j);
                String name = lObj.getString("n");
                JSONArray s = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s");
                double value = (s.isNull(0) ? 0 : s.getDouble(0)) / scale;

                sum += value;

                children.put(JSONObject.create().put("name", name).put("value", value));
            }

            data.put(JSONObject.create().put("name", tObj.getString("n")).put("value", sum).put("children", children));
        }

        return series.put(JSONObject.create().put("data", data).put("name", this.getDimensionNameByID(targetIDs[0])).put("dimensionID", targetIDs[0]));
    }

    public String getSeriesType(String dimensionID){
        return "treeMap";
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE;
    }
}
