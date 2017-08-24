package com.fr.bi.cal.analyze.report.report.widget.table;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


/**
 * Created by GUY on 2015/4/9.
 */
public class BITableReportSetting extends BIAbstractTableSetting {
    private static final long serialVersionUID = -8748078323275198680L;
    @BICoreField
    protected String[] summary;
    @BICoreField
    protected String[] column;
    @BICoreField
    protected String[] row;

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
            ArrayList<String> dimensionList = new ArrayList<String>();

            for (Map.Entry<String, String[]> entry : getSortedList()) {
                int regionValue = Integer.parseInt(entry.getKey());
                if (regionValue >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) && regionValue < Integer.parseInt(BIReportConstant.REGION.DIMENSION2)) {
                    Collections.addAll(dimensionList, entry.getValue());
                }
            }
            row = dimensionList.toArray(new String[dimensionList.size()]);
        }
        if (groups_of_dimensions.size() > 1) {
            ArrayList<String> dimensionList = new ArrayList<String>();
            for (Map.Entry<String, String[]> entry : getSortedList()) {
                int regionValue = Integer.parseInt(entry.getKey());
                if (regionValue >= Integer.parseInt(BIReportConstant.REGION.DIMENSION2) && regionValue < Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
                    Collections.addAll(dimensionList, entry.getValue());
                }
            }
            column = dimensionList.toArray(new String[dimensionList.size()]);

        }
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
     * 获取复杂列表的行表头数据
     */
    public ArrayList<ArrayList<String>> getComplex_x_dimension() {
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        for (Map.Entry<String, String[]> entry : getSortedList()) {
            int regionType = Integer.parseInt(entry.getKey());
            if (regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) && regionType < Integer.parseInt(BIReportConstant.REGION.DIMENSION2)) {
                if (entry.getValue().length > 0) {
                    ArrayList<String> groupList = new ArrayList<String>();
                    Collections.addAll(groupList, entry.getValue());
                    list.add(groupList);
                }
            }
        }
        return list;
    }

    /**
     * 获取复杂列表的纵向表头
     */
    public ArrayList<ArrayList<String>> getComplex_y_dimension() {
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        for (Map.Entry<String, String[]> entry : getSortedList()) {
            int regionType = Integer.parseInt(entry.getKey());
            if (regionType >= Integer.parseInt(BIReportConstant.REGION.DIMENSION2) && regionType < Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
                if (entry.getValue().length > 0) {
                    ArrayList<String> groupList = new ArrayList<String>();
                    Collections.addAll(groupList, entry.getValue());
                    list.add(groupList);
                }
            }
        }
        return list;
    }

    private ArrayList<Map.Entry<String, String[]>> getSortedList() {
        ArrayList<Map.Entry<String, String[]>> groups_of_dimension_list = new ArrayList<Map.Entry<String, String[]>>(groups_of_dimensions.entrySet());
        Collections.sort(groups_of_dimension_list, new Comparator<Map.Entry<String, String[]>>() {
            @Override
            public int compare(Map.Entry<String, String[]> o1, Map.Entry<String, String[]> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return groups_of_dimension_list;
    }

}