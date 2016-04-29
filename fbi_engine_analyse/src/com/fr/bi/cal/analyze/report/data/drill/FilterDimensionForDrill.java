package com.fr.bi.cal.analyze.report.data.drill;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-10-11
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class FilterDimensionForDrill implements JSONParser {
    protected Map dimensionMap = new HashMap();

    /**
     * 转成json
     *
     * @param jo jsonobject对象
     * @throws com.fr.json.JSONException
     */
    @Override
    public void parseJSON(JSONObject jo) throws JSONException {
        if (jo == null) {
            return;
        }
        dimensionMap.clear();
        JSONArray ja = jo.getJSONArray("widget_value");
        for (int i = 0, len = ja.length(); i < len; i++) {
            JSONObject j = ja.getJSONObject(i);
            JSONArray names = j.names();
            for (int k = 0, klen = names.length(); k < klen; k++) {
                String name = names.getString(k);
                dimensionMap.put(name, j.get(name));
            }
        }
    }

    public Map getDimensionMap() {
        return this.dimensionMap;
    }
}