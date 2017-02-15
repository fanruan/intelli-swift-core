package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.table;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class TableDataForExport {
    private List<ReportExportItem> items;
    private List<ReportExportHeader> header;
    private List<ReportExportItem> crossItems;
    private List<ReportExportItem> crossHeader;

    public TableDataForExport(List<ReportExportItem> items, List<ReportExportHeader> header, List<ReportExportItem> crossItems, List<ReportExportItem> crossHeader) {
        this.items = items;
        this.header = header;
        this.crossItems = crossItems;
        this.crossHeader = crossHeader;
    }
}
