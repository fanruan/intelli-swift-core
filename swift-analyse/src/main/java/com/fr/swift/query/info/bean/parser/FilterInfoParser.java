package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.NotFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.GeneralFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
class FilterInfoParser {

    static FilterInfo parse(FilterInfoBean bean) {
        if (null != bean) {
            return null;
        }
        switch (bean.getType()) {
            case AND:
            case OR:
                List<FilterInfoBean> filterInfoBeans = (List<FilterInfoBean>) ((GeneralFilterInfoBean) bean).getFilterValue();
                List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
                if (null != filterInfoBeans) {
                    for (FilterInfoBean filterInfoBean : filterInfoBeans) {
                        filterInfos.add(parse(filterInfoBean));
                    }
                }
                return new GeneralFilterInfo(filterInfos, bean.getType() == SwiftDetailFilterType.OR ? GeneralFilterInfo.OR : GeneralFilterInfo.AND);
            case NOT:
                FilterInfoBean filterInfoBean = ((NotFilterBean) bean).getFilterValue();
                FilterInfo filterInfo = parse(filterInfoBean);
                return new NotFilterInfo(filterInfo);
            default:
                DetailFilterInfoBean detailFilterInfoBean = (DetailFilterInfoBean) bean;
                ColumnKey columnKey = new ColumnKey(detailFilterInfoBean.getColumn());
                columnKey.setRelation(RelationSourceParser.parse(detailFilterInfoBean.getRelation()));
                return new SwiftDetailFilterInfo(columnKey, detailFilterInfoBean.getFilterValue(), detailFilterInfoBean.getType());
        }
    }
}
