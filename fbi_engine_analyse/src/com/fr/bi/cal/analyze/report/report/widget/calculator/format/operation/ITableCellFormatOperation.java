package com.fr.bi.cal.analyze.report.report.widget.calculator.format.operation;

import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/5/12.
 */
public interface ITableCellFormatOperation {
    String formatItemTextValues(String text) throws Exception;
    JSONObject createItemTextStyle(String text) throws Exception;
    String formatHeaderText(String headerText) throws Exception;
}
