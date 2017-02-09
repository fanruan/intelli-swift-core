package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelSheetData;

import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/9.
 */
public interface ExcelInterface {
    JSONObject parseCHartOptions() throws Exception;

    JSONObject getChartOptions();
}
