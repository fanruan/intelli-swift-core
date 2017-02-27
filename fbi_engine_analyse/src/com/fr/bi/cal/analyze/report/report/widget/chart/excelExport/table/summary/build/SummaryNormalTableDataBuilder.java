package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/16.
 */
public class SummaryNormalTableDataBuilder extends SummaryTableDataBuilder {


    public SummaryNormalTableDataBuilder(TableWidget widget, JSONObject dataJSON) throws Exception {
        super(widget, dataJSON);
    }


    @Override
    public void initAttrs() throws JSONException {
super.initAttrs();
    }

    @Override
    public void createHeadersAndItems() throws Exception {
        tableWithoutDims();
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items, crossHeaders, crossItems);
        return tableDataForExport;

    }


}
