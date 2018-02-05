package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringContainFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoBelongFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.DetailFilterInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/21.
 */
public class FilterFactory {

    public static FilterInfo transformFilter(List<FilterBean> beans) {
        List<SwiftDetailFilterValue> filterValues = new ArrayList<SwiftDetailFilterValue>();
        for (FilterBean bean : beans) {
            filterValues.add(createFilterValue(bean));
        }
        return new DetailFilterInfo(filterValues);
    }

    private static SwiftDetailFilterValue createFilterValue(FilterBean bean) {
        String fieldName = ((AbstractFilterBean) bean).getFieldName();
        int type = bean.getFilterType();
        switch (type) {
            case BICommonConstants.ANALYSIS_FILTER_STRING.BELONG_VALUE:
                List<String> belongValues = ((StringBelongFilterBean) bean).getFilterValue().getValue();
                return new SwiftDetailFilterValue<Set<String>>(fieldName,
                        new HashSet<String>(belongValues), SwiftDetailFilterType.STRING_IN);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BELONG_VALUE:
                List<String> notBelongValues = ((StringNoBelongFilterBean) bean).getFilterValue().getValue();
                return new SwiftDetailFilterValue<Set<String>>(fieldName,
                        new HashSet<String>(notBelongValues), SwiftDetailFilterType.STRING_NOT_IN);
            case BICommonConstants.ANALYSIS_FILTER_STRING.CONTAIN:
                String contain = ((StringContainFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String>(fieldName, contain, SwiftDetailFilterType.STRING_LIKE);
            default:
        }
        return null;
    }

}
