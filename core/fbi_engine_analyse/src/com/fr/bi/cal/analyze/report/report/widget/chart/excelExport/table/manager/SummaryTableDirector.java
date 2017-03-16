package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public class SummaryTableDirector {
    IExcelDataBuilder builder;

    public SummaryTableDirector(IExcelDataBuilder builder) {
        this.builder = builder;
    }
    public void construct() throws Exception {
        builder.initAttrs();
        builder.createHeadersAndItems();
    }
    public BIExcelTableData buildTableData() throws JSONException {
        return builder.createTableData();
    }
}
