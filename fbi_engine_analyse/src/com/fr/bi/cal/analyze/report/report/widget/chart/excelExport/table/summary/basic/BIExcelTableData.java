package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 * todo 少用json
 */
public class BIExcelTableData implements JSONCreator {
    private List<BITableHeader> headers;
    private JSONArray items;
    private JSONArray crossHeaders;
    private JSONArray crossItems;

    public BIExcelTableData(List<BITableHeader> headers, JSONArray items, JSONArray crossHeaders, JSONArray crossItems) {
        this.headers = headers;
        this.items = items;
        this.crossHeaders = crossHeaders;
        this.crossItems = crossItems;
    }


    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
    }

    public List<BITableHeader> getHeaders() {
        return headers;
    }

    public JSONArray getItems() {
        return items;
    }

    public JSONArray getCrossHeaders() {
        return crossHeaders;
    }

    public JSONArray getCrossItems() {
        return crossItems;
    }
}
