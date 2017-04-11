package com.fr.bi.cal.analyze.report.report.widget.chart.export.manager;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIExcelTableData;
import com.fr.json.JSONException;

/**
 * Created by Kary on 2017/2/26.
 */
public class TableDirector {
    IExcelDataBuilder builder;

    public TableDirector(IExcelDataBuilder builder) {
        this.builder = builder;
    }
    public void construct() throws Exception {
        builder.initAttrs();
        builder.createHeaders();
        builder.createItems();
    }
    public BIExcelTableData buildTableData() throws JSONException {
        return builder.createTableData();
    }
}
