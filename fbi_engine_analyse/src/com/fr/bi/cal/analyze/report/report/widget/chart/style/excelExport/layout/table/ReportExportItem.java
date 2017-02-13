package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.table;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class ReportExportItem {

    private String dId;
    private String text;
    private String[] value;
    private boolean isCross;
    private boolean needExpand;
    private List<ReportExportItem> children;
    private List<ReportExportItem> clicked;
    private boolean isSum;

    public ReportExportItem() {
    }
}
