package com.fr.bi.field.dimension.filter;

import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.dimension.filter.field.DimensionTargetValueFilter;
import com.fr.bi.field.dimension.filter.field.EmptyDimensionTargetValueFilter;
import com.fr.bi.field.dimension.filter.formula.EmptyFormulaFilter;
import com.fr.bi.field.dimension.filter.formula.FormulaValueFilter;
import com.fr.bi.field.dimension.filter.general.GeneralANDDimensionFilter;
import com.fr.bi.field.dimension.filter.general.GeneralORDimensionFilter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/3.
 */
public class DimensionFilterFactory {
    public static DimensionFilter parseFilter(JSONObject jo, long userId) throws Exception {
        DimensionFilter filter = null;
        if(jo.has(BIJSONConstant.JSON_KEYS.FILTER_TYPE)){
            switch (jo.getInt(BIJSONConstant.JSON_KEYS.FILTER_TYPE)){
                case BIReportConstant.FILTER_TYPE.AND:
                    filter = new GeneralANDDimensionFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.OR:
                    filter = new GeneralORDimensionFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.FORMULA:
                    filter = new FormulaValueFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.EMPTY_FORMULA:
                    filter = new EmptyFormulaFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new EmptyDimensionTargetValueFilter();
                    break;
                default:
                    filter = new DimensionTargetValueFilter();
                    break;
            }
        }
        if (filter != null){
            filter.parseJSON(jo, userId);
        }
        return filter;
    }
}