package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.UUID;

/**
 * Created by User on 2016/8/31.
 */
public class CompareBarChartSetting extends BIAbstractBarChartSetting {

    public CompareBarChartSetting() throws JSONException {
        super(new JSONArray().put(new JSONObject()
                .put("type", "value")
                .put("title", new JSONObject()
                        .put("style", new JSONObject("{" +
                                "fontFamily:\"" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "\"," +
                                "color:\"" + BIChartSettingConstant.FONT_STYLE.COLOR + "\"," +
                                "fontSize:\"" + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "\"}"
                        )))
                .put("labelStyle", new JSONObject("{" +
                        "fontFamily:\"" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "\"," +
                        "color:\"" + BIChartSettingConstant.FONT_STYLE.COLOR + "\"," +
                        "fontSize:\"" + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "\"}"
                )).put("formatter",  "function () { return this > 0 ? this : (-1) * this;}")));
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        String uuid = UUID.randomUUID().toString();
        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < data.length(); ){
                JSONObject it = item.getJSONObject(j);
                JSONArray da = it.getJSONArray("data");
                for(int k = 0; k < da.length(); k++){
                    JSONObject t = da.getJSONObject(k);
                    String tmp = t.getString("y");
                    t.put("y", t.getString("x"));
                    t.put("x", tmp);
                    if (i == 0) {
                        t.put("x", -t.getDouble("x"));
                    }
                }
                it.put("stack", uuid);
            }
        }
        return super.formatItems(data, types, options);
    }
}
