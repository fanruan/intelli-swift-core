package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
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
                firstSeries.put("fillColorOpacity", 0).put("stack", "stack0");
            }
            JSONArray firstDatas = firstSeries.optJSONArray("data");

            for (int i = 1, len = series.length(); i < len; i++) {
                JSONObject ser = series.optJSONObject(i);
                ser.put("stack", "stack0");

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

        if(series == null || series.length() == 0){
            return;
        }

        JSONObject firstSeries = series.optJSONObject(0);
        JSONArray firstDatas = firstSeries.optJSONArray("data");

        for (int seriesIndex = 1, len = series.length(); seriesIndex < len; seriesIndex++) {
            JSONObject ser = series.getJSONObject(seriesIndex);

            JSONArray datas = ser.optJSONArray("data");
            for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex++){
                JSONObject d = datas.optJSONObject(dataIndex);
                double y = firstDatas.optJSONObject(dataIndex).optDouble("y", 0);

                JSONObject labels = new JSONObject(dataLabels.toString());
                BISummaryTarget target = this.getSerBITarget(ser);
                String format = this.valueFormat(target);
                if(labels.has("formatter")) {
                    String unit = this.valueUnit(target, false);
                    labels.optJSONObject("formatter")
                            .put("valueFormat", String.format("function(){return BI.contentFormat(arguments[0] + %s , \"%s\") + \"%s\"}", y, format, unit))
                            .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}");
                }
                d.put(dataLabelsKey(), labels);

                JSONObject formatter = JSONObject.create();
                String tooltipUnit = this.valueUnit(target, true);

                formatter.put("identifier", this.getTooltipIdentifier())
                        .put("valueFormat", String.format("function(){return BI.contentFormat(arguments[0] + %s , \"%s\") + \"%s\"}", y, format, tooltipUnit));
                d.put("tooltip", new JSONObject(tooltip.toString()).put("formatter", formatter));
            }
        }
    }

    //和标签一起处理了
    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
    }


    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException {
        return super.parseLeftValueAxis(settings).put("reversed", false);
    }

}
