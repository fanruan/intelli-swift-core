package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class StringNotInFilter implements IFilter{

    private final int MULTI = 1;
    private final int ALL = 2;
    private int type;
    private Set<String> valueSet;

    public StringNotInFilter(JSONObject param) throws JSONException{
        if(param.has("type")){
            type = param.optInt("type", MULTI);
        }
        if(param.has("value")){
            valueSet = new HashSet<String>();
            JSONArray tmp = param.optJSONArray("value");
            if(tmp != null){
                for (int i = 0; i < tmp.length(); i++) {
                    valueSet.add(tmp.getString(i));
                }
            }
        }
    }

    private boolean isStringNotIn(String value){
        if(this.valueSet == null || this.valueSet.isEmpty()){
            return false;
        }
        return !this.valueSet.contains(value);
    }

    private boolean isAllSelect(){
        return this.type == ALL;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(isStringNotIn(array.getString(i))){
                result.put(array.getString(i));
            }
        }
        return result;
    }
}
