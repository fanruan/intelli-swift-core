package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by eason on 2017/2/27.
 */
public class VanTreeMapWidget extends VanChartWidget{
    private JSONArray firstRegionArray = JSONArray.create();
    private JSONArray secondRegionArray = JSONArray.create();

    protected void dealView(List<String> sorted, JSONObject vjo) throws JSONException {
        int firstRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION2);
        int secondRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION1);

        for (String region : sorted) {

            if (Integer.parseInt(region) == firstRegion) {
                firstRegionArray = vjo.optJSONArray(region);
                vjo.remove(region);
            }

            if (Integer.parseInt(region) == secondRegion) {
                secondRegionArray = vjo.optJSONArray(region);
                vjo.remove(region);
            }
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, firstRegionArray);
        vjo.put(BIReportConstant.REGION.DIMENSION2, secondRegionArray);
    }

    private boolean hasRegionUsed(JSONArray array) {
        if(array == null){
            return false;
        }
        for(int i = 0, len = array.length(); i < len; i++){
            String id = array.optString(i);
            for (BIDimension dimension : getDimensions()) {
                if (ComparatorUtils.equals(dimension.getId(), id) && dimension.isUsed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("zoom", settings.optBoolean("clickZoom"));
        plotOptions.put("borderWidth", 1);

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {
        String[] dimensionIDs = this.getUsedDimensionID();

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();

        if(targetIDs.length == 0){
            return series;
        }

        double scale = this.numberScale(targetIDs[0]);

        boolean hasFirstLevel = hasRegionUsed(firstRegionArray);
        boolean hasSecondLevel = hasRegionUsed(secondRegionArray);

        JSONArray data = JSONArray.create();
        if(originData.has("t") && originData.has("l")){//两个维度、一个指标
            createTopLeftSeries(originData, scale, data, targetIDs[0]);
        } else if(originData.has("c")){//一个维度、一个指标
            createChildrenSeries(originData, scale, hasFirstLevel, hasSecondLevel, data, targetIDs[0]);
        } else if(originData.has("s")){//一个指标
            createTargetSeries(originData, scale, data, targetIDs[0]);
        }

        JSONObject sery = JSONObject.create();
        if(hasSecondLevel){
            sery.put("name", getDimensionNameByID(targetIDs[0]));
        }
        return series.put(sery.put("data", data)
                .put("dimensionIDs", dimensionIDs).put("targetIDs", JSONArray.create().put(targetIDs[0])));
    }

    private void createTopLeftSeries(JSONObject originData, double scale, JSONArray data, String id) throws JSONException{
        JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
        JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");
        BIDimension seriesDim = this.getSeriesDimension(), categoryDim = this.getCategoryDimension();

        for (int i = 0; i < topC.length(); i++) {
            JSONArray children = JSONArray.create();

            JSONObject tObj = topC.getJSONObject(i);
            double sum = 0;
            for (int j = 0; j < leftC.length(); j++) {
                JSONObject lObj = leftC.getJSONObject(j);
                String name = lObj.getString("n"), formattedName = this.formatDimension(categoryDim, name);
                JSONArray s = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s");
                double value = (s.isNull(0) ? 0 : s.getDouble(0)) / scale;

                sum += value;

                children.put(JSONObject.create().put("name", formattedName).put(LONG_DATE, name).put("value", numberFormat(id, value)));
            }

            String name = tObj.getString("n"), formattedName = this.formatDimension(seriesDim, name);

            data.put(JSONObject.create().put("name", formattedName).put(LONG_DATE, name).put("value", sum).put("children", children));
        }
    }

    private void createChildrenSeries(JSONObject originData, double scale,
                                      boolean hasFirstLevel, boolean hasSecondLevel, JSONArray data, String id) throws JSONException{
        JSONArray childrenC = originData.optJSONArray("c");


        JSONArray children = JSONArray.create();
        double sum = 0;

        for (int j = 0; j < childrenC.length(); j++) {
            JSONObject lObj = childrenC.getJSONObject(j);
            BIDimension dimension = hasFirstLevel ? this.getCategoryDimension() : this.getSeriesDimension();
            String name = lObj.getString("n"), formattedName = this.formatDimension(dimension, name);

            JSONArray s = lObj.getJSONArray("s");
            double value = (s.isNull(0) ? 0 : s.getDouble(0)) / scale;

            if(hasFirstLevel) {
                sum += value;
                children.put(JSONObject.create().put("name", formattedName).put(LONG_DATE, name).put("value", numberFormat(id,value)));
            } else if(hasSecondLevel){
                data.put(JSONObject.create().put("name", formattedName).put(LONG_DATE, name).put("value", numberFormat(id,value)));
            }
        }

        if(hasFirstLevel) {
            data.put(JSONObject.create().put("value", sum).put("children", children));
        }
    }

    private void createTargetSeries(JSONObject originData, double scale, JSONArray data, String id) throws JSONException{
        JSONArray targetValues = originData.optJSONArray("s");
        double y = targetValues.isNull(0) ? 0 : targetValues.getDouble(0) / scale;
        data.put(JSONObject.create().put("value", numberFormat(id,y)));
    }

    public String getSeriesType(String dimensionID){
        return "treeMap";
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE + PERCENT;
    }

    protected String categoryLabelKey() {
        return NAME;
    }
}