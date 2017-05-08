package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.bi.conf.report.widget.IWidgetStyle;
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
    private List<ITableItem> items;
    private List<ITableHeader> crossHeaders;
    private JSONArray crossItems;
    private IWidgetStyle widgetStyle;

    public BIExcelTableData(List<ITableHeader> headers, List<ITableItem> items, List<ITableHeader> crossHeaders, JSONArray crossItems, IWidgetStyle widgetStyle) {
        this.headers = headers;
        this.items = items;
        this.crossHeaders = crossHeaders;
        this.crossItems = crossItems;
        this.widgetStyle = widgetStyle;
    }

    public BIExcelTableData(List<ITableHeader> headers, List<ITableItem> items, IWidgetStyle widgetStyle) {
        this.headers = headers;
        this.items = items;
        this.widgetStyle = widgetStyle;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray headerArray = new JSONArray();
        for (ITableHeader header : headers) {
            headerArray.put(header.createJSON());
        }
        jo.put("header", headerArray);
        if (null != crossHeaders) {
            JSONArray crossHeaderArray = new JSONArray();
            for (ITableHeader header : crossHeaders) {
                crossHeaderArray.put(header.createJSON());
            }
            jo.put("crossHeader", crossHeaderArray);
        }
        if (null != items) {
            JSONArray itemArray = new JSONArray();
            for (ITableItem item : items) {
                itemArray.put(item.createJSON());
            }
            jo.put("items", itemArray);
        }
        if (null != crossItems) {
            jo.put("crossItems", crossItems);
        }
        return jo;
    }


    public List<ITableHeader> getHeaders() {
        return headers;
    }

    public List<ITableItem> getItems() {
        return items;
    }

    public List<ITableHeader> getCrossHeaders() {
        return crossHeaders;
    }

    public JSONArray getCrossItems() {
        return crossItems;
    }

    public IWidgetStyle getWidgetStyle() {
        return widgetStyle;
    }
}
