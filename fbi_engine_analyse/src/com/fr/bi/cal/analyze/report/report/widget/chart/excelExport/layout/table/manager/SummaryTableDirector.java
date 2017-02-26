package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.manager;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic.BIExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.summary.basic.BIExcelTableData;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public class SummaryTableDirector {
    BIExcelDataBuilder builder;

    public SummaryTableDirector(BIExcelDataBuilder builder) {
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
