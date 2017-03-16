package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public interface IExcelDataBuilder {
    void initAttrs() throws JSONException;

    void createHeadersAndItems() throws Exception;

    BIExcelTableData createTableData() throws JSONException;
}
