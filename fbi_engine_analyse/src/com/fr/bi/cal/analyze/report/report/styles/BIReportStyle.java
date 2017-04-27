package com.fr.bi.cal.analyze.report.report.styles;

import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/7.
 */
public class BIReportStyle implements IReportStyle {
    private String themeStyle;
    private int wsTableStyle;

    public BIReportStyle() {
        themeStyle= BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle= BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
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
        themeStyle = jo.has("themeStyle") ? jo.getString("themeStyle") : BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle = jo.has("wsTableStyle") ? jo.getInt("wsTableStyle") : BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
    }
}
