package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BISummaryCellStyle;

/**
 * Created by Kary on 2017/2/26.
 */
public class SummaryTableStyleHelper {
    public static BISummaryCellStyle getHeaderStyles(String themeColor, String styleType) {
        return new BISummaryCellStyle(null, null, null);
    }
    public static BISummaryCellStyle getBodyStyles(String themeColor, String styleType) {
        return new BISummaryCellStyle(null, null, null);
    }
    public static BISummaryCellStyle getLastSummaryStyles(String themeColor, String styleType) {
        return new BISummaryCellStyle(null, null, null);
    }
}
