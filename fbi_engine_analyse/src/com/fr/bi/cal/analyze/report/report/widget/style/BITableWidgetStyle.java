package com.fr.bi.cal.analyze.report.report.widget.style;

import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/4/27.
 */
public class BITableWidgetStyle implements IWidgetStyle {
    private static final long serialVersionUID = -675827399089527747L;
    private boolean showNumber;
    private boolean freezeCols;
    private List<Integer> mergeCols;
    private List<Integer> columnSize;
    private int headerRowSize;
    private int footerRowSize;
    private int rowSize;
    private boolean showRowToTal;
    private String themeStyle;
    private int tableStyleGroup;
    private String themeColor;
    private int wsTableStyle;
    private JSONArray chartColor;


    public BITableWidgetStyle() {
        themeStyle = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        tableStyleGroup = BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
        showNumber = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_NUMBER;
        freezeCols = BIStyleConstant.DEFAULT_CHART_SETTING.FREEZE_DIM;
        headerRowSize = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        footerRowSize = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        rowSize = BIStyleConstant.DEFAULT_CHART_SETTING.MAX_ROW;
        showRowToTal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_ROW_TOTAL;
        mergeCols = new ArrayList<Integer>();
        columnSize = new ArrayList<Integer>();
        themeColor = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle = BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
        chartColor=new JSONArray();
    }

    public boolean isShowNumber() {
        return showNumber;
    }

    public boolean isFreezeCols() {
        return freezeCols;
    }

    public List<Integer> getMergeCols() {
        return mergeCols;
    }

    public List<Integer> getColumnSize() {
        return columnSize;
    }

    public int getHeaderRowSize() {
        return headerRowSize;
    }

    public int getFooterRowSize() {
        return footerRowSize;
    }

    public int getRowSize() {
        return rowSize;
    }

    public boolean isShowRowToTal() {
        return showRowToTal;
    }

    public String getThemeStyle() {
        return themeStyle;
    }

    public int getTableStyleGroup() {
        return tableStyleGroup;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public int getWsTableStyle() {
        return wsTableStyle;
    }

    public JSONArray getChartColor() {
        return chartColor;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("settings")) {
            JSONObject settingJo = jo.getJSONObject("settings");
            showNumber = settingJo.optBoolean("showNumber");
            freezeCols = settingJo.optBoolean("freezeFirstColumn");
            headerRowSize = settingJo.optInt("rowHeight");
            footerRowSize = settingJo.optInt("rowHeight");
            rowSize = settingJo.optInt("maxRow");
            if (settingJo.has("mergeCols")) {
                JSONArray array = settingJo.optJSONArray("mergeCols");
                for (int i = 0; i < array.length(); i++) {
                    mergeCols.add((Integer) array.get(i));
                    columnSize.add((Integer) array.get(i));
                }
            }
            showRowToTal = settingJo.optBoolean("showRowToTal");
            themeStyle = settingJo.optString("themeStyle");
            tableStyleGroup = settingJo.optInt("tableStyleGroup");
            themeColor = settingJo.optString("themeColor");
            wsTableStyle = settingJo.optInt("charStyle");
            chartColor=settingJo.optJSONArray("charColor");
        }
    }

}


