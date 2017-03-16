package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic;

import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableStyle extends JSONCreator{
    void parse(JSONObject style);
}
