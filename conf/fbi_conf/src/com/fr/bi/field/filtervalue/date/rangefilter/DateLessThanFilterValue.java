package com.fr.bi.field.filtervalue.date.rangefilter;

import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.data.key.date.DateRange;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2016/5/21.
 */
public class DateLessThanFilterValue extends DateInRangeFilterValue{
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("filter_value") ) {
            range = new DateRange(null, new BIDay(jo.getLong("filter_value")));
        }
    }
}
