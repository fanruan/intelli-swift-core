package com.fr.bi.cal.analyze.report.report.widget.chart.filter.condition;

import com.fr.bi.cal.analyze.report.report.widget.chart.filter.FilterFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * or condition
 * Created by AstronautOO7 on 2016/9/19.
 */
public class GeneralOrFilter implements IFilter {
    private IFilter[] childs;

    public GeneralOrFilter(JSONObject jo) throws JSONException {
        JSONArray filterValue;
        if (jo.has("filterValue")) {
            filterValue = jo.getJSONArray("filterValue");
            childs = new IFilter[filterValue.length()];
            for (int i = 0; i < filterValue.length(); i++) {
                childs[i] = FilterFactory.parseFilter(filterValue.getJSONObject(i));
            }
        }
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        Set<String> result = new HashSet<String>();
        for (int i = 0; i < childs.length; i++) {
            JSONArray ja = childs[i].getFilterResult(array);
            for (int k = 0; k < ja.length(); k++) {
                String key = ja.getString(k);
                result.add(key);
            }
        }
        return new JSONArray(result.toString());
    }
}
