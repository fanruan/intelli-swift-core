package com.fr.bi.field.target.filter;

import com.fr.bi.field.target.filter.field.*;
import com.fr.bi.field.target.filter.formula.FormulaFilter;
import com.fr.bi.field.target.filter.general.GeneralANDFilter;
import com.fr.bi.field.target.filter.general.GeneralORFilter;
import com.fr.bi.field.target.filter.tree.TreeColumnFieldsFilter;
import com.fr.bi.stable.constant.BIReportConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 小灰灰 on 2016/1/8.
 */
public class BIFilterMap {
    public static final Map<Class, Integer> ALL_TYPES = new HashMap<Class, Integer>(){{
        put(GeneralANDFilter.class, BIReportConstant.FILTER_TYPE.AND);
        put(GeneralORFilter.class, BIReportConstant.FILTER_TYPE.OR);
        put(FormulaFilter.class, BIReportConstant.FILTER_TYPE.FORMULA);
        put(TreeColumnFieldsFilter.class, BIReportConstant.FILTER_TYPE.TREE_FILTER);
        put(ColumnFieldFilter.class, BIReportConstant.FILTER_TYPE.COLUMNFILTER);
    }};

}