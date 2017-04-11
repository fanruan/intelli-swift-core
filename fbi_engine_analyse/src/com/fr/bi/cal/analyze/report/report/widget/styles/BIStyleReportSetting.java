package com.fr.bi.cal.analyze.report.report.widget.styles;

import com.fr.bi.stable.constant.BIGlobalStyleConstant;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/7.
 */
public class BIStyleReportSetting implements BIStyleSetting {
    private String themeStyle;
    private int wsTableStyle;

    public BIStyleReportSetting() {
        themeStyle=BIGlobalStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle=BIGlobalStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
    }

    @Override
    public String getThemeStyle() {
        return themeStyle;
    }

    @Override
    public int getWsTableStyle() {
        return wsTableStyle;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        themeStyle = jo.has("themeStyle") ? jo.getString("themeStyle") : BIGlobalStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle = jo.has("wsTableStyle") ? jo.getInt("wsTableStyle") : BIGlobalStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
    }
}
