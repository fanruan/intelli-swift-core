package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;

import java.util.List;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableItem extends JSONCreator{
    JSONArray getValue();

    List<ITableItem> getChildren();

    void setValue(JSONArray value);

    void setChildren(List<ITableItem> children);

    boolean hasValues();
}
