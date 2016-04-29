package com.fr.bi.field.target.filter;

import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.field.ColumnFieldFilter;
import com.fr.bi.field.target.filter.field.ColumnNameFilter;
import com.fr.bi.field.target.filter.field.SummaryNumberFilter;
import com.fr.bi.field.target.filter.formula.FormulaFilter;
import com.fr.bi.field.target.filter.general.GeneralANDFilter;
import com.fr.bi.field.target.filter.general.GeneralORFilter;
import com.fr.bi.field.target.filter.tree.TreeColumnFieldsFilter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/7.
 */
public class TargetFilterFactory {
    /**
     * 获取过滤结果
     *
     * @param jo     json对象
     * @param userid 用户信息
     * @return 过滤结果
     * @throws Exception
     */
    public static TargetFilter parseFilter(JSONObject jo, long userid) throws Exception {
        TargetFilter filter = null;
        if(jo.has(BIJSONConstant.JSON_KEYS.FILTER_TYPE)){
            switch (jo.getInt(BIJSONConstant.JSON_KEYS.FILTER_TYPE)){
                case BIReportConstant.FILTER_TYPE.AND:
                    filter = new GeneralANDFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.OR:
                    filter = new GeneralORFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.FORMULA:
                case BIReportConstant.FILTER_TYPE.EMPTY_FORMULA:
                    filter = new FormulaFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.TREE_FILTER:
                    filter = new TreeColumnFieldsFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.NUMBER_SUM:
                case BIReportConstant.FILTER_TYPE.NUMBER_AVG:
                case BIReportConstant.FILTER_TYPE.NUMBER_MAX:
                case BIReportConstant.FILTER_TYPE.NUMBER_MIN:
                case BIReportConstant.FILTER_TYPE.NUMBER_COUNT:
                    filter = new SummaryNumberFilter();
                    break;
                case BIReportConstant.FILTER_TYPE.EMPTY_CONDITION:
                    filter = null;
                    break;
                default:
                    filter = jo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT) ? new ColumnFieldFilter() : new ColumnNameFilter();
            }
        }
        if (filter != null) {
            filter.parseJSON(jo, userid);
        }
        return filter;
    }
}