package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.objectcondition;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/9/22.
 */
public abstract class AbstractXYZFilter implements IFilter{

    public String getKey(){
        return "";
    }

    public boolean contains(JSONArray array, JSONObject obj){
        for(int i = 0; i < array.length(); i++){
            JSONObject arrObj = array.optJSONObject(i);
            if(obj.optDouble("x") == arrObj.optDouble("x")
                    && obj.optDouble("y") == arrObj.optDouble("y")
                    && obj.optDouble("z") == arrObj.optDouble("z")){
                return true;

            }
        }
        return false;
    }
}
