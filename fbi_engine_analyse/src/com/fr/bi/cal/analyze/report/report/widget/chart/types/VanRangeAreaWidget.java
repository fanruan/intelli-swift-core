package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanRangeAreaWidget extends VanAreaWidget{

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = super.createSeries(originData);

        if(series != null && series.length() > 0) {
            String valueKey = valueKey();

            JSONObject firstSeries = series.optJSONObject(0);
            if(series.length() > 1) {
                firstSeries.put("fillColorOpacity", 0).put("stack", 0);
            }
            JSONArray firstDatas = firstSeries.optJSONArray("data");

            for (int i = 1, len = series.length(); i < len; i++) {
                JSONObject ser = series.optJSONObject(i);
                ser.put("stack", 0);

                JSONArray datas = ser.optJSONArray("data");
                for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex++){
                    JSONObject d = datas.optJSONObject(dataIndex);
                    d.put(valueKey, d.optDouble(valueKey) - firstDatas.optJSONObject(dataIndex).optDouble(valueKey));
                }

            }
        }

        return series;
    }

    protected int yAxisIndex(String dimensionID) {
        return 0;
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject(dataLabelsKey());
        JSONObject tooltip = options.optJSONObject("plotOptions").optJSONObject("tooltip");


        JSONArray series = options.optJSONArray("series");

        JSONObject firstSeries = series.optJSONObject(0);
        JSONArray firstDatas = firstSeries.optJSONArray("data");

        for (int i = 1, len = series.length(); i < len; i++) {
            JSONObject ser = series.getJSONObject(i);

            JSONArray datas = ser.optJSONArray("data");
            for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex++){
                JSONObject d = datas.optJSONObject(dataIndex);
                JSONObject labels = new JSONObject(dataLabels.toString());
                String format = this.valueFormat(this.getSerBITarget(ser), false);
                double y = firstDatas.optJSONObject(i).optDouble("y");
                labels.optJSONObject("formatter")
                        .put("valueFormat", String.format("function(){return BI.contentFormat(arguments[0] + %s , \"%s\")}", y, format))
                        .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}");

                d.put(dataLabelsKey(), labels);

                JSONObject formatter = JSONObject.create();
                String tooltipFormat = this.valueFormat(this.getSerBITarget(ser), true);

                formatter.put("identifier", this.getTooltipIdentifier())
                        .put(this.tooltipValueKey(), String.format("function(){return BI.contentFormat(arguments[0] + %s , \"%s\")}", y, tooltipFormat));

                d.put("tooltip", new JSONObject(tooltip.toString()).put("formatter", formatter));
            }
        }
    }

    //和标签一起处理了
    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
    }

}
