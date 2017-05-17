package com.fr.bi.cal.analyze.report.report.widget.style;

import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/27.
 */
public class BITableWidgetStyle implements IWidgetStyle {

    private boolean freezeDim;
    private boolean showNumber;
    private int rowHeight;
    private int maxRow;
    private boolean showRowTotal;
    private boolean showColTotal;
    private String themeColor;
    private int tableStyleGroup;
    private boolean showColTotal;

    public BITableWidgetStyle() {
        themeColor = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        tableStyleGroup = BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
        showNumber = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_NUMBER;
        freezeDim = BIStyleConstant.DEFAULT_CHART_SETTING.FREEZE_DIM;
        rowHeight = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        maxRow = BIStyleConstant.DEFAULT_CHART_SETTING.MAX_ROW;
        showRowTotal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_ROW_TOTAL;
        showColTotal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_COL_TOTAL;
    }

    @Override
    public boolean isShowNumber() {
        return false;
    }

    @Override
    public int getRowHeight() {
        return rowHeight;
    }

    @Override
    public int getRowSize() {
        return maxRow;
    }

    @Override
    public boolean isShowRowTotal() {
        return showRowTotal;
    }

    @Override
    public String getThemeColor() {
        return themeColor;
    }

    @Override
    public int getTableStyleGroup() {
        return tableStyleGroup;
    }

    @Override
    public boolean isShowColTotal() {
        return showColTotal;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("settings")) {
            JSONObject settingJo = jo.getJSONObject("settings");
            showNumber = settingJo.optBoolean("showNumber", showNumber);
            freezeDim = settingJo.optBoolean("freezeDim", freezeDim);
            rowHeight = settingJo.optInt("rowHeight", rowHeight);
            maxRow = settingJo.optInt("maxRow", maxRow);
            showRowTotal = settingJo.optBoolean("showRowTotal", showRowTotal);
            showColTotal = settingJo.optBoolean("showColTotal", showColTotal);
            themeColor = settingJo.optString("themeColor", themeColor);
            tableStyleGroup = settingJo.optInt("tableStyleGroup", tableStyleGroup);
        }
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("showNumber", showNumber);
        jo.put("freezeDim", freezeDim);
        jo.put("rowHeight", rowHeight);
        jo.put("maxRow", maxRow);
        jo.put("showRowTotal", showRowTotal);
        jo.put("showColTotal", showColTotal);
        jo.put("themeColor", themeColor);
        jo.put("tableStyleGroup", tableStyleGroup);
        return jo;
    }

    public boolean isShowColTotal() {
        return showColTotal;
    }
}


