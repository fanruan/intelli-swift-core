package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanTreeMapWidget extends VanChartWidget{

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        JSONObject tooltip = plotOptions.optJSONObject("tooltip");
        tooltip.put("shared", true);

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();

        if(targetIDs.length == 0){
            return series;
        }

        double scale = this.numberScale(targetIDs[0]);
        JSONArray data = JSONArray.create();

        JSONObject sery = JSONObject.create();

        if(!originData.has("t") && originData.has("s")){
            JSONArray targetValues = originData.optJSONArray("s");
            double y = targetValues.isNull(0) ? 0 : targetValues.getDouble(0) / scale;
            data.put(JSONObject.create().put("value", y));
        } else {

            JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
            JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");

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

            sery.put("name", this.getSeriesDimension().getText());
        }

        return series.put(sery.put("data", data).put("dimensionID", targetIDs[0]));
    }

    public String getSeriesType(String dimensionID){
        return "treeMap";
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE;
    }

    protected String categoryLabelKey() {
        return NAME;
    }
}
