package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SumaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build.SummaryNormalTableDataBuilder;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by Kary on 2017/2/26.
 */
public class ExcelExportDataBuildFactory {
    public static BIExcelTableData createExportData(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON) throws Exception {
        IExcelDataBuilder builder;
        if (dataJSON.has("t")) {
            builder = new SumaryCrossTableDataBuilder(dimAndTar, dataJSON);
        } else {
            builder = new SummaryNormalTableDataBuilder(dimAndTar, dataJSON);
        }
        SummaryTableDirector director = new SummaryTableDirector(builder);
        director.construct();
        return director.buildTableData();
    }

    public static Map<Integer, List<JSONObject>> createViewMap(TableWidget widget) throws Exception {
        Map<Integer, List<String>> view = widget.getWidgetView();
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        Iterator<Integer> iterator = view.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<JSONObject> list = new ArrayList<JSONObject>();
            List<String> ids = view.get(next);
            for (String dId : ids) {
                int type = widget.getFieldTypeByDimensionID(dId);
                String text = widget.getDimensionNameByID(dId);
                list.add(new JSONObject().put("dId", dId).put("text", text).put("type", type));
            }
            dimAndTar.put(next, list);
        }
        return dimAndTar;
    }

}
