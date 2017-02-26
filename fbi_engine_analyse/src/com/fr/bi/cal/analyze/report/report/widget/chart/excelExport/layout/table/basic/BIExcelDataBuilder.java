package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.summary.basic.BIExcelTableData;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public interface BIExcelDataBuilder {
    void initAttrs() throws JSONException;

    void createHeadersAndItems() throws Exception;

    BIExcelTableData createTableData() throws JSONException;
}
