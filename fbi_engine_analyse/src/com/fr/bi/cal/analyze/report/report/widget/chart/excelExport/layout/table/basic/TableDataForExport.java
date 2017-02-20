package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic;

import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/13.
 */
public class TableDataForExport {
    private Map items;
    private List<ReportTableHeader> header;
    private Map crossItems;
    private List<ReportTableHeader> crossHeader;

    public TableDataForExport(Map items, List<ReportTableHeader> header, Map crossItems, List<ReportTableHeader> crossHeader) {
        this.items = items;
        this.header = header;
        this.crossItems = crossItems;
        this.crossHeader = crossHeader;
    }

    public JSONObject createJsonObject() {
        return new JSONObject();
    }
}
