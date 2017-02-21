package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic;

import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class TableDataForExport {
    private List<ReportItem> items;
    private List<ReportTableHeader> header;
    private List<ReportItem> crossItems;
    private List<ReportTableHeader> crossHeader;

    public TableDataForExport(List<ReportItem> items, List<ReportTableHeader> header, List<ReportItem> crossItems, List<ReportTableHeader> crossHeader) {
        this.items = items;
        this.header = header;
        this.crossItems = crossItems;
        this.crossHeader = crossHeader;
    }

    public JSONObject createJsonObject() {
        return new JSONObject();
    }
}
