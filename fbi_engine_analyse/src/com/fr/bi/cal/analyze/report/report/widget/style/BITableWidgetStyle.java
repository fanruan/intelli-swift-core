package com.fr.bi.cal.analyze.report.report.widget.style;

import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by Kary on 2017/4/27.
 */
public class BITableWidgetStyle implements IWidgetStyle {

    private boolean freezeDim;
    private boolean showNumber;
    private int rowHeight;
    private int maxRow;
    private boolean showRowTotal;
    private String themeColor;
    private int tableStyleGroup;
    private boolean showColTotal;
    private int tableFormGroup;
    private JSONArray columnSize;
    private boolean showName;
    private int namePos;
    private JSONArray regionColumnSize;

    public BITableWidgetStyle() {
        themeColor = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        tableStyleGroup = BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
        showNumber = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_NUMBER;
        freezeDim = BIStyleConstant.DEFAULT_CHART_SETTING.FREEZE_DIM;
        rowHeight = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        maxRow = BIStyleConstant.DEFAULT_CHART_SETTING.MAX_ROW;
        showRowTotal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_ROW_TOTAL;
        showColTotal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_COL_TOTAL;
        tableFormGroup = BIStyleConstant.TABLE_FORM.OPEN_ROW;
        columnSize = new JSONArray();
        showName =  BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_NAME;
        namePos = BIStyleConstant.DEFAULT_CHART_SETTING.NAME_POS;
        regionColumnSize = new JSONArray();
    }

    @Override
    public boolean isShowNumber() {
        return showNumber;
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
    public int getTableFormGroup() {
        return tableFormGroup;
    }

    @Override
    public boolean isShowName() {
        return showName;
    }

    @Override
    public int getNamePos() {
        return namePos;
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
            //无色
            if (StringUtils.isEmpty(themeColor)) {
                themeColor = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
            }
            tableStyleGroup = settingJo.optInt("tableStyleGroup", tableStyleGroup);
            tableFormGroup = settingJo.optInt("tableFormGroup", tableFormGroup);
            columnSize = settingJo.optJSONArray("columnSize");
            showName = settingJo.optBoolean("showName", showName);
            namePos = settingJo.optInt("namePos", namePos);
            regionColumnSize = settingJo.optJSONArray("regionColumnSize");
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
        jo.put("columnSize", columnSize);
        jo.put("regionColumnSize", regionColumnSize);
        return jo;
    }
}


