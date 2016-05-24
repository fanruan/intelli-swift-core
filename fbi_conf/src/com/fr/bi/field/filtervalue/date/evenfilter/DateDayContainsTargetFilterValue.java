package com.fr.bi.field.filtervalue.date.evenfilter;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.data.key.date.BIDayValue;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashSet;

/**
 * Created by 小灰灰 on 2016/5/22.
 */
public class DateDayContainsTargetFilterValue extends DateKeyTargetFilterValue{
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        this.group = BIReportConstant.GROUP.YMD;
        valueSet = new HashSet<BIDateValue>();
        if (jo.has("filter_value")) {
            JSONObject valueOb = jo.getJSONObject("filter_value");
            JSONArray ja = valueOb.getJSONArray("value");
            for (int i = 0, len = ja.length(); i < len; i++) {
                valueSet.add(new BIDayValue(DateUtils.parse(ja.getString(i)).getTime()));
            }
        }
    }
}
