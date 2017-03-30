package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.ITableItem;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/3/30.
 */
public class BIDetailTableItem implements ITableItem {
    private List<ITableItem> children;

    public List<ITableItem> getChildren() {
        return children;
    }

    public void setChildren(List<ITableItem> children) {
        this.children = children;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray childs = new JSONArray();
        for (ITableItem child : children) {
            childs.put(child.createJSON());
        }
        return new JSONObject().put("children", childs);
    }
}
