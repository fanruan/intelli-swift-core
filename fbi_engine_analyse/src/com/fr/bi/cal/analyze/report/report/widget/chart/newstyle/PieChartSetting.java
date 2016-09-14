package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class PieChartSetting extends BIAbstractChartSetting {

    private JSONObject config;

    public PieChartSetting() {
        this.config = new JSONObject();
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        this.config.put("color", options.getJSONArray("chart_color"));
        this.config.put("style", this.formatChartStyle(options.getInt("chart_style")));
        this.formatChartPieStyle(this.config, options.getInt("chart_pie_type"), options.getInt("chart_inner_radius"), options.getInt("chart_total_angle"));
        this.formatChartLegend(options.optInt("chart_legend"));
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));

        plotOptions.getJSONObject("tooltip").getJSONObject("formatter").put("identifier", "${CATEGORY}${SERIES}${VALUE}${PERCENT}");
        this.config.put("chartType", "pie");
        plotOptions.getJSONObject("dataLabels").put("align", "outside").put("connectorWidth", "outside").getJSONObject("formatter").put("identifier", "${VALUE}${PERCENT}");
        for(int i = 0; i < data.length(); i++){
            JSONArray da = data.getJSONObject(i).getJSONArray("data");
            for(int j = 0; j < da.length(); j++){
                JSONObject num = da.getJSONObject(j);
                num.put("y", this.formatXYDataWithMagnify(num.getDouble("y"), 1));
            }
        }
        return this.config.put("series", data);
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        if(this.isNeedConvert(data)){
            //把每个坐标轴所有的多个系列合并成一个系列
            JSONArray result = new JSONArray();
            for(int i = 0; i < data.length(); i++){
                JSONArray item = data.getJSONArray(i);
                JSONArray seriesItem = new JSONArray();
                JSONObject obj = new JSONObject().put("data", new JSONArray()).put("name", "");
                seriesItem.put(obj);
                for(int j = 0; j < item.length(); j++){
                    JSONObject series = item.getJSONObject(j);
                    JSONArray da = series.getJSONArray("data");
                    for(int k = 0; k < da.length(); k++){
                        JSONObject d = da.getJSONObject(k);
                        obj.getJSONArray("data").put(d.put("x", series.getString("name")));
                    }
                }
                result.put(seriesItem);
            }
            return super.formatItems(result, types, options);
        }else{
            return super.formatItems(data, types, options);
        }
    }

    //目前饼图不会有多个系列，如果有多个就要把它们合并在一起
    private boolean isNeedConvert(JSONArray items) throws JSONException{
        JSONArray result = null;
        for(int i = 0; i < items.length(); i++){
            JSONArray item = items.getJSONArray(i);
            if(item.length() > 1){
                result = item;
                break;
            }
        }
        return result != null;
    }
}
