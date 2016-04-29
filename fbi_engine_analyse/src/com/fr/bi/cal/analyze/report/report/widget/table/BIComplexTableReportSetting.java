package com.fr.bi.cal.analyze.report.report.widget.table;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GUY on 2015/4/9.
 */
public class BIComplexTableReportSetting extends BITableReportSetting {

    private ArrayList<ArrayList<String>> complex_x_dimension;
    private ArrayList<ArrayList<String>> complex_y_dimension;


    /**
     * 获取复杂列表的行表头数据
     */
    @Override
    public ArrayList<ArrayList<String>> getComplex_x_dimension() {
        if (complex_x_dimension == null) {
            complex_x_dimension = new ArrayList<ArrayList<String>>();
        }

        return complex_x_dimension;
    }

    /**
     * 获取复杂列表的纵向表头
     */
    @Override
    public ArrayList<ArrayList<String>> getComplex_y_dimension() {
        if (complex_y_dimension == null) {
            complex_y_dimension = new ArrayList<ArrayList<String>>();
        }

        return complex_y_dimension;
    }

    /**
     * 获取列的最大占宽
     *
     * @return
     */
    public int getMaxColumnLen() {
        if (complex_y_dimension.isEmpty()) {
            return 0;
        }
        int len = complex_y_dimension.get(0).size();
        for (int i = 1; i < complex_y_dimension.size(); i++) {
            if (len < complex_y_dimension.get(i).size()) {
                len = complex_y_dimension.get(i).size();
            }
        }

        return len;
    }

    /**
     * 获取行的最大占高
     *
     * @return
     */
    public int getMaxRowLen() {
        if (complex_x_dimension.isEmpty()) {
            return 0;
        }
        int len = complex_x_dimension.get(0).size();
        for (int i = 1; i < complex_x_dimension.size(); i++) {
            if (len < complex_x_dimension.get(i).size()) {
                len = complex_x_dimension.get(i).size();
            }
        }

        return len;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("complex_data")) {
            JSONObject complexData = jo.getJSONObject("complex_data");

            if (complexData.has("x_view")) {

                JSONArray xView = complexData.getJSONArray("x_view");
                complex_x_dimension = new ArrayList<ArrayList<String>>();

                for (int i = 0; i < xView.length(); i++) {
                    JSONArray jsonArray = xView.getJSONArray(i);
                    ArrayList<String> oneArray = new ArrayList<String>();

                    if (jsonArray.length() == 0) {
                        continue;
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        oneArray.add(jsonArray.getString(j));
                    }

                    complex_x_dimension.add(oneArray);
                }
            }

            if (complexData.has("y_view")) {

                JSONArray xView = complexData.getJSONArray("y_view");
                complex_y_dimension = new ArrayList<ArrayList<String>>();

                for (int i = 0; i < xView.length(); i++) {
                    JSONArray jsonArray = xView.getJSONArray(i);
                    ArrayList<String> oneArray = new ArrayList<String>();

                    if (jsonArray.length() == 0) {
                        continue;
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        oneArray.add(jsonArray.getString(j));
                    }

                    complex_y_dimension.add(oneArray);
                }
            }
        }
    }
}