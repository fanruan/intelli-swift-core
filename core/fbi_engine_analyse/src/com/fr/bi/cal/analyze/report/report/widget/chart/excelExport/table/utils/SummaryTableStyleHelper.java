package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BITableItemStyle;

/**
 * Created by Kary on 2017/2/26.
 */
public class SummaryTableStyleHelper {
    public static BITableItemStyle getHeaderStyles(String themeColor, String styleType) {
        return new BITableItemStyle(null, null, null);
    }
    public static BITableItemStyle getBodyStyles(String themeColor, String styleType) {
        return new BITableItemStyle(null, null, null);
    }
    public static BITableItemStyle getLastSummaryStyles(String themeColor, String styleType) {
        return new BITableItemStyle(null, null, null);
    }
}
