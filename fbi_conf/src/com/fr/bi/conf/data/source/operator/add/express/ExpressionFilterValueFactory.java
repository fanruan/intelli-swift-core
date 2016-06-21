package com.fr.bi.conf.data.source.operator.add.express;

import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.field.filtervalue.date.rangefilter.DateInRangeFilterValue;
import com.fr.bi.field.filtervalue.number.rangefilter.NumberInRangeFilterValue;
import com.fr.bi.field.filtervalue.string.rangefilter.StringINFilterValue;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2016/5/10.
 */
public class ExpressionFilterValueFactory {
    public static  FilterValue createRowValue(JSONObject jo, int type) throws Exception {
        FilterValue filter = null;
        switch (type){
            case DBConstant.COLUMN.STRING :
                filter = new StringINFilterValue();
                break;
            case DBConstant.COLUMN.NUMBER:
                filter = new NumberInRangeFilterValue();
                break;
            default:
                filter = new DateInRangeFilterValue();
        }
        filter.parseJSON(jo, UserControl.getInstance().getSuperManagerID());
        return filter;
    }

}
