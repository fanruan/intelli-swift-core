package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.BIExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SummaryNormalTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SummaryTableDataBuilder;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/26.
 */
public class ExcelExportDataBuildFactory {
    public static BIExcelTableData createExprotData(TableWidget widget, JSONObject dataJSON) throws Exception {
        BIExcelDataBuilder builder;
        if (dataJSON.has("t")){
            builder = new SummaryTableDataBuilder(widget, dataJSON);
        }else {
           builder=new SummaryNormalTableDataBuilder(widget, dataJSON);
        }
        SummaryTableDirector director = new SummaryTableDirector(builder);
        director.construct();
        return director.buildTableData();
    }
}
