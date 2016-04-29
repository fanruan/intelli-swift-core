package com.fr.bi.field.filtervalue;

import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/8.
 */
public class FilterValueFactory {
    /**
     * 创建数值过滤值
     *
     * @param jo     json对象
     * @param userId 用户id
     * @return 过滤值
     * @throws Exception
     */
    public static FilterValue parseFilterValue(JSONObject jo, long userId) throws Exception {
        FilterValue filterValue = null;
        if (jo.has("filter_type")) {
            int type = jo.getInt("filter_type");
            if(type != -1){
                filterValue = (FilterValue)(BIFilterValueMap.ALL_VALUES.get(type)).newInstance();
                if (filterValue != null) {
                    filterValue.parseJSON(jo, userId);
                }
            }
        }
        return filterValue;
    }
}