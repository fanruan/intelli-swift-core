package com.fr.bi.cal.analyze.report.report.widget.table;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by GUY on 2015/4/9.
 */
public class BITableReportSetting extends BIAbstractTableSetting {
    protected String[] summary;
    protected String[] column;
    protected String[] row;
    private boolean freeze;
    private int number;
    private int tableStyle;
    private ArrayList<ArrayList<String>> complex_x_dimension;
    private ArrayList<ArrayList<String>> complex_y_dimension;

    /**
     * 组
     *
     * @param jo 注
     *           complex_data: {
     *           x_view: [],
     *           y_view: [],
     *           x_all: [],
     *           y_all: []
     *           }
     * @return 住
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (groups_of_targets.size() > 0) {
            summary = groups_of_targets.get(BIReportConstant.REGION.TARGET1);
        }
        if (groups_of_dimensions.size() > 0) {
            row = groups_of_dimensions.get(BIReportConstant.REGION.DIMENSION1);
        }
        if (groups_of_dimensions.size() > 1) {
            column = groups_of_dimensions.get(BIReportConstant.REGION.DIMENSION2);
        }
        if (jo.has("style")) {
            JSONObject jo1 = jo.optJSONObject("style");
            if (jo1.has("tableStyle")) {
                tableStyle = jo1.optInt("tableStyle");
            }
            if (jo1.has("freeze")) {
                freeze = jo1.optBoolean("freeze", false);
            }
            if (jo1.has("number")) {
                number = jo1.optInt("number", 0);
            }
        }
    }

    public int getTableStyle() {
        return this.tableStyle;
    }

    @Override
    public String[] getSummary() {
        if (summary == null) {
            summary = new String[0];
        }
        return summary;
    }

    /**
     * 获取行内容
     *
     * @return
     */
    @Override
    public String[] getRow() {
        if (row == null) {
            row = new String[0];
        }
        return row;
    }

    /**
     * 获取列内容
     *
     * @return
     */
    @Override
    public String[] getColumn() {
        if (column == null) {
            column = new String[0];
        }
        return column;
    }

    @Override
    public boolean useRealData() {
        return true;
    }

    /**
     * 有无编号
     *
     * @return 编号
     */
    @Override
    public int isOrder() {
        return number;
    }

    /**
     * 获取复杂列表的行表头数据
     */
    public ArrayList<ArrayList<String>> getComplex_x_dimension() {
        if (complex_x_dimension == null) {
            complex_x_dimension = new ArrayList<ArrayList<String>>();
        }

        return complex_x_dimension;
    }

    /**
     * 获取复杂列表的纵向表头
     */
    public ArrayList<ArrayList<String>> getComplex_y_dimension() {
        if (complex_y_dimension == null) {
            complex_y_dimension = new ArrayList<ArrayList<String>>();
        }

        return complex_y_dimension;
    }
}