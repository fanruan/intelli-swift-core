package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.condition;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.FilterFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * and condition
 * Created by AstronautOO7 on 2016/9/19.
 */
public class GeneralAndFilter implements IFilter {
    private IFilter[] childs;

    public GeneralAndFilter(JSONObject jo) throws JSONException {
        JSONArray filter_value;
        if (jo.has("filter_value")) {
            filter_value = jo.getJSONArray("filter_value");
            childs = new IFilter[filter_value.length()];
            for (int i = 0; i < filter_value.length(); i++) {
                childs[i] = FilterFactory.parseFilter(filter_value.getJSONObject(i));
            }
        }
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        JSONArray result = new JSONArray();
        for (int i = 0; i < childs.length; i++) {
            JSONArray ja = childs[i].getFilterResult(array);
            if (i == 0) {
                for (int j = 0; j < ja.length(); j++) {
                    map.put(ja.getString(j), 0);
                }
                continue;
            }
            for (int k = 0; k < ja.length(); k++) {
                String key = ja.getString(k);
                if (key != null) {
                    map.put(key, map.get(key) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if(entry.getValue() == childs.length){
                result.put(entry.getKey());
            }
        }
        return result;
    }
}
