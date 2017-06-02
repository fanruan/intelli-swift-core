package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/5/12.
 */
public interface ITableCellFormatOperation {
    String formatTextValues(String text) throws Exception;
    JSONObject createTextStyle(String text) throws Exception;
}
