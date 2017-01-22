package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.objectcondition;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class XYZGeneralAndFilter extends AbstractXYZFilter {

    private String key = UUID.randomUUID().toString();
    private AbstractXYZFilter[] childs;

    public XYZGeneralAndFilter(JSONObject jo) throws JSONException{
        JSONArray filter_value;
        if (jo.has("filter_value")) {
            filter_value = jo.optJSONArray("filter _value");
            childs = new AbstractXYZFilter[filter_value.length()];
            for (int i = 0; i < filter_value.length(); i++) {
                childs[i] = FilterBubbleScatterFactory.parseFilter(filter_value.getJSONObject(i));
            }
        }
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        if(array.length() == 0){
            return new JSONArray();
        }
        Map<String, JSONArray> filterValueMap = new HashMap<String, JSONArray>();
        for (AbstractXYZFilter child : childs){
            filterValueMap.put(child.getKey(), child.getFilterResult(array));
        }
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            JSONObject item = array.getJSONObject(i);
            boolean needPush = true;
            for (AbstractXYZFilter child : childs) {
                if(child != null && !contains(filterValueMap.get(child.getKey()), item)){
                    needPush = false;
                    break;
                }
            }
            if(needPush){
                result.put(item);
            }
        }
        return result;
    }
}