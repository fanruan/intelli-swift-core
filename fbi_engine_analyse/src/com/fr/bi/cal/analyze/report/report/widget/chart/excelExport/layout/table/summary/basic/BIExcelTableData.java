package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.summary.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/13.
 */
public class BIExcelTableData implements JSONCreator {
    private JSONArray headers;
    private JSONArray items;
    private JSONArray crossHeaders;
    private JSONArray crossItems;

    public BIExcelTableData(JSONArray headers, JSONArray items, JSONArray crossHeaders, JSONArray crossItems) {
        this.headers = headers;
        this.items = items;
        this.crossHeaders = crossHeaders;
        this.crossItems = crossItems;
    }


    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
    }
}
