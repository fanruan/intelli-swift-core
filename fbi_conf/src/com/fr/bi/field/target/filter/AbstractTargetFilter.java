package com.fr.bi.field.target.filter;

import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2016/1/8.
 */
public abstract class AbstractTargetFilter implements TargetFilter{
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
	public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("filter_type", BIFilterMap.ALL_TYPES.get(getClass()));
        return jo;
    }
}