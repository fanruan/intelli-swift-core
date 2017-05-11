package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableItem extends JSONCreator {
    String getDId();

    String getText();

    JSONArray getValues();

    List<ITableItem> getChildren();

    void setValues(JSONArray value);

    void setChildren(List<ITableItem> children);

    boolean hasValues();

    void parseJSON(JSONObject jo) throws Exception;
}
