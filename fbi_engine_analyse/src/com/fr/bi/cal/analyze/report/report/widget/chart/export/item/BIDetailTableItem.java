package com.fr.bi.cal.analyze.report.report.widget.chart.export.item;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/3/30.
 */
public class BIDetailTableItem extends BIBasicTableItem {


    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray childArray= new JSONArray();
        for (ITableItem child : children) {
            childArray.put(child.createJSON());
        }
        return new JSONObject().put("children", childArray);
    }
}
