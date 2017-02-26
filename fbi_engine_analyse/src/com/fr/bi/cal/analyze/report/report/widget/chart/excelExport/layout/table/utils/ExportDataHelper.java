package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.utils;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;

/**
 * Created by Kary on 2017/2/26.
 */
public class ExportDataHelper {
    // FIXME: 2017/2/26 需要抽象出来
    public static int getFieldTypeByDimensionID(TableWidget widget, String dId) throws Exception {
        return widget.getFieldTypeByDimensionID(dId);
    }
    public static String getDimensionNameByID(TableWidget widget, String dId) throws Exception {
        return widget.getDimensionNameByID(dId);
    }
}
