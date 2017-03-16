package com.fr.bi.field.filtervalue.date.evenfilter;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.data.key.date.BIDayValue;
import com.fr.json.JSONObject;

import java.util.HashSet;

/**
 * Created by 小灰灰 on 2016/5/24.
 */
public class DateDayTargetFilterValue  extends DateKeyTargetFilterValue{
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        this.group = BIReportConstant.GROUP.YMD;
        valueSet = new HashSet<BIDateValue>();
        if (jo.has("filter_value")) {
            valueSet.add(new BIDayValue(jo.getLong("filter_value")));
        }
    }
}
