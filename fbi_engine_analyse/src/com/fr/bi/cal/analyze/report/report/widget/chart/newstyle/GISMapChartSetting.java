package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class GISMapChartSetting extends BIAbstractChartSetting {

    private JSONObject config;

    public GISMapChartSetting() {
        this.config = new JSONObject();
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        config.remove("dataSheet");
        config.remove("legend");
        config.remove("zoom");
        JSONObject plotOptions = new JSONObject();
        plotOptions.getJSONObject("dataLabels").put("enabled", options.getBoolean("show_data_label"))
                .put("useHtml", true).put("formatter", "function () { " +
                "var name = (BI.isArray(this.name) ? '' : this.name + ',') + (window.BH ? BH.contentFormat(this.value, '#.##') : this.value);" +
                "var style = \"padding: 5px; background-color: rgba(0,0,0,0.4980392156862745);border-color: rgb(0,0,0); border-radius:2px; border-width:0px;\";" +
                "var a = '<div style = ' + style + '>' + name + '</div>';" +
                "return a");
        plotOptions.getJSONObject("tooltip").put("shared", true).put("formatter", "function () {" +
                "var tip = BI.isArray(this.name) ? '' : this.name;" +
                "for(int i = 0; i < this.points.length; i++){tip += ('<div>' + this.points[i].seriesName + ':' + (window.BH ? BH.contentFormat((this.points[i].size || this.points[i].y), '#.##') : (this.points[i].size || this.points[i].y)) + '</div>');}" +
                "return tip;}");
        config.put("geo", new JSONObject("{" +
                "\"tileLayer\": \"http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}\"," +
                "\"attribution\": \"<a><img src='http://webapi.amap.com/theme/v1.3/mapinfo_05.png'>&copy; 2016 AutoNavi</a>\"}"
        ));
        config.put("chartType", "pointMap");
        plotOptions.put("icon", new JSONObject("{" +
                "iconUrl: \"" + BIChartSettingConstant.GIS_ICON_PATH + "\"," +
                "iconSize: [24, 24]" +
                "}"));

        plotOptions.put("marker", new JSONObject("{" +
                "symbol:\"" + BIChartSettingConstant.GIS_ICON_PATH + "\"," +
                "width: 24," +
                "height: 24," +
                "enable: true" +
                "}"));
        config.remove("xAxis");
        config.remove("yAxis");
        return this.config.put("series", data);
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }

    private boolean checkLngLatValid(JSONArray lnglat) throws JSONException{
        if (lnglat.length() < 2) {
            return false;
        }
        return lnglat.getDouble(0) <= 180 && lnglat.getDouble(0) >= -180 && lnglat.getDouble(1) <= 90 && lnglat.getDouble(1) >= -90;
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONArray results = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            JSONArray result = new JSONArray();
            for(int j = 0; j < item.length(); j++){
                JSONArray res = new JSONArray();
                JSONObject it = item.getJSONObject(j);
                JSONArray da = it.getJSONArray("data");
                for(int k = 0; k < da.length(); k++){
                    JSONObject d = da.getJSONObject(k);
                    d.put("y", this.formatXYDataWithMagnify(d.getDouble("y"), 1));
                    String[] lnglat = d.getString("x").split(",");
                    if(options.getInt("lnglat") == BIChartSettingConstant.LAT_FIRST){
                        String lng = lnglat[1];
                        lnglat[1] = lnglat[0];
                        lnglat[0] = lng;
                    }
                    d.put("lnglat", lnglat);
                    d.put("value", d.getDouble("y"));
                    d.put("name", d.optString("z") != null ? d.optString("z") : d.getString("lnglat"));
                    if (this.checkLngLatValid(d.getJSONArray("lnglat"))) {
                        res.put(d);
                    }
                }
                if (res.length() != 0) {
                    result.put(it.put("data", res));
                }
            }
            if (result.length() != 0) {
                results.put(result);
            }
        }
        return super.formatItems(results, types, options);
    }
}
