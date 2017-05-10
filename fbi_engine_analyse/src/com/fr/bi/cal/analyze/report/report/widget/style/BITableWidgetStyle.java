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
//    @PersistNameHistory(historyNames = {"showSequence"})
    private boolean showSequence;
    private boolean freezeCols;
    private List<Integer> mergeCols;
    private List<Integer> columnSize;
    private int headerRowSize;
    private int footerRowSize;
    private int rowSize;
    private boolean showRowToTal;
    private String themeStyle;
    private int wsTableStyle;

    public BITableWidgetStyle() {
        themeStyle = BIStyleConstant.DEFAULT_CHART_SETTING.THEME_COLOR;
        wsTableStyle = BIStyleConstant.DEFAULT_CHART_SETTING.TABLE_STYLE_GROUP;
        showSequence = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_NUMBER;
        freezeCols = BIStyleConstant.DEFAULT_CHART_SETTING.FREEZE_DIM;
        headerRowSize = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        footerRowSize = BIStyleConstant.DEFAULT_CHART_SETTING.ROW_HEIGHT;
        rowSize = BIStyleConstant.DEFAULT_CHART_SETTING.MAX_ROW;
        showRowToTal = BIStyleConstant.DEFAULT_CHART_SETTING.SHOW_ROW_TOTAL;
        mergeCols = new ArrayList<Integer>();
        columnSize = new ArrayList<Integer>();
    }


    public boolean isFreezeCols() {
        return freezeCols;
    }

    @Override
    public boolean isShowNumber() {
        return false;
    }

    @Override
    public List<?> getMergeCols() {
        return mergeCols;
    }

    @Override
    public List<Integer> getColumnSize() {
        return columnSize;
    }

    @Override
    public int getHeaderRowSize() {
        return headerRowSize;
    }

    @Override
    public int getFooterRowSize() {
        return footerRowSize;
    }

    @Override
    public int getRowSize() {
        return rowSize;
    }

    @Override
    public boolean isShowRowToTal() {
        return showRowToTal;
    }

    @Override
    public String getThemeStyle() {
        return themeStyle;
    }

    @Override
    public int getTableStyleGroup() {
        return wsTableStyle;
    }

    @Override
    public int getWsTableStyle() {
        return wsTableStyle;
    }


    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("settings")) {
            JSONObject settingJo = jo.getJSONObject("settings");
            showSequence = settingJo.optBoolean("showNumber");
            freezeCols = settingJo.optBoolean("freezeDim");
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
            wsTableStyle = settingJo.optInt("wsTableStyle");
        }
    }

}


