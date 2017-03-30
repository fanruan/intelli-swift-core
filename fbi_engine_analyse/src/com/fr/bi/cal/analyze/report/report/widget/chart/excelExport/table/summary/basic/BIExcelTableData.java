package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.ITableHeader;
import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 * todo 少用json
 */
public class BIExcelTableData implements JSONCreator {
    private List<ITableHeader> headers;
    private JSONArray items;
    private List<ITableHeader> crossHeaders;
    private JSONArray crossItems;

    public BIExcelTableData(List<ITableHeader> headers, JSONArray items, List<ITableHeader> crossHeaders, JSONArray crossItems) {
        this.headers = headers;
        this.items = items;
        this.crossHeaders = crossHeaders;
        this.crossItems = crossItems;
    }

    public BIExcelTableData(List<ITableHeader> headers, JSONArray items) {
        this.headers = headers;
        this.items = items;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray headerArray=new JSONArray();
        for (ITableHeader header : headers) {
           headerArray.put(header.createJSON());
        }
        jo.put("header", headerArray);
        jo.put("items", items);
        JSONArray croosHeaderArray=new JSONArray();
        for (ITableHeader header : crossHeaders) {
            croosHeaderArray.put(header.createJSON());
        }
        jo.put("crossHeader", croosHeaderArray);
        jo.put("crossItems", crossItems);
        return jo;
    }


    public List<ITableHeader> getHeaders() {
        return headers;
    }

    public JSONArray getItems() {
        return items;
    }

    public List<ITableHeader> getCrossHeaders() {
        return crossHeaders;
    }

    public JSONArray getCrossItems() {
        return crossItems;
    }
}
