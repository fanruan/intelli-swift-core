package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.ITableItem;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 * todo 少用json
 */
public class BISummaryDataConstructor implements DataConstructor {
    protected int widgetType = WidgetType.TABLE.getType();
    protected List<ITableHeader> headers;
    protected List<ITableItem> items;
    private List<ITableHeader> crossHeaders;
    private List<ITableItem> crossItems;
    private BIWidgetStyle settings;

    public BISummaryDataConstructor(List<ITableHeader> headers, List<ITableItem> items, List<ITableHeader> crossHeaders, List<ITableItem> crossItems, BIWidgetStyle widgetStyle) {
        this.headers = headers;
        this.items = items;
        this.crossHeaders = crossHeaders;
        this.crossItems = crossItems;
        this.settings = widgetStyle;
    }

    public BISummaryDataConstructor(List<ITableHeader> headers, List<ITableItem> items, BIWidgetStyle widgetStyle) {
        this.headers = headers;
        this.items = items;
        this.settings = widgetStyle;
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
            JSONArray itemArray = new JSONArray();
            for (ITableItem item : crossItems) {
                itemArray.put(item.createJSON());
            }
            jo.put("crossItems", itemArray);
        }
        if (null != settings) {
            jo.put("settings", settings.createJSON());
        }
        return jo;
    }

    @Override
    public int getWidgetType() {
        return this.widgetType;
    }

    @Override
    public List<ITableHeader> getHeaders() {
        return headers;
    }

    @Override
    public List<ITableItem> getItems() {
        return items;
    }

    @Override
    public List<ITableHeader> getCrossHeaders() {
        return crossHeaders;
    }

    @Override
    public List<ITableItem> getCrossItems() {
        return crossItems;
    }

    @Override
    public BIWidgetStyle getWidgetStyle() {
        return settings;
    }
}
