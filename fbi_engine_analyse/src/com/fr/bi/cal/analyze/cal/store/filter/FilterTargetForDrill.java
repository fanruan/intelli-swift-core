package com.fr.bi.cal.analyze.cal.store.filter;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;


/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-10-11
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class FilterTargetForDrill extends FilterDimensionForDrill {
    private String targetName;

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

        JSONObject jsonObject = jo.getJSONObject("widget_value");
        if (jsonObject.has("target")) {
            targetName = jsonObject.getString("target");
        }
        dimensionMap.clear();
        if (jsonObject.has("value")) {
            JSONArray ja = jsonObject.getJSONArray("value");

            for (int i = 0, len = ja.length(); i < len; i++) {
                JSONObject j = ja.getJSONObject(i);
                JSONArray names = j.names();
                for (int k = 0, klen = names.length(); k < klen; k++) {
                    String name = names.getString(k);
                    dimensionMap.put(name, j.get(name));
                }
            }
        }
    }

    public String getTargetName() {
        return this.targetName;
    }

}