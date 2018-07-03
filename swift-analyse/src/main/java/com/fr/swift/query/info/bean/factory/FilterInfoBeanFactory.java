package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.GeneralFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class FilterInfoBeanFactory implements BeanFactory<List<FilterInfo>, List<FilterInfoBean>> {

    public static final BeanFactory<FilterInfo, FilterInfoBean> SINGLE_FILTER_INFO_BEAN_FACTORY = new BeanFactory<FilterInfo, FilterInfoBean>() {
        @Override
        public FilterInfoBean create(FilterInfo source) {
            if (source == null) {
                return null;
            }
            FilterInfoBean result = null;
            if (source instanceof GeneralFilterInfo) {
                int type = ((GeneralFilterInfo) source).getType();
                if (type == GeneralFilterInfo.AND) {
                    result = new AndFilterBean();
                } else if (type == GeneralFilterInfo.OR) {
                    result = new OrFilterBean();
                }
                ((GeneralFilterInfoBean) result).setFilterValue(FilterInfoBeanFactory.getInstance().create(((GeneralFilterInfo) source).getChildren()));
                ((GeneralFilterInfoBean) result).setType(type == GeneralFilterInfo.OR ? SwiftDetailFilterType.OR : SwiftDetailFilterType.AND);
            } else if (source instanceof SwiftDetailFilterInfo) {
                switch (((SwiftDetailFilterInfo) source).getType()) {
                    case IN:
                        result = new InFilterBean();
                        break;
                    case STRING_STARTS_WITH:
                    case STRING_ENDS_WITH:
                    case STRING_LIKE:
                        result = new StringOneValueFilterBean();
                        break;
                    case NUMBER_IN_RANGE:
                        result = new NumberInRangeFilterBean();
                        break;
                    case TOP_N:
                    case BOTTOM_N:
                        result = new NFilterBean();
                        break;
                    case NULL:
                        result = new NFilterBean();
                        break;
                }
                ((DetailFilterInfoBean) result).setType(((SwiftDetailFilterInfo) source).getType());
                ((DetailFilterInfoBean) result).setFilterValue(((SwiftDetailFilterInfo) source).getFilterValue());
                ColumnKey columnKey = ((SwiftDetailFilterInfo) source).getColumnKey();
                if (null != columnKey) {
                    ((DetailFilterInfoBean) result).setColumn(columnKey.getName());
                    ((DetailFilterInfoBean) result).setRelation(RelationSourceBeanFactory.SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(columnKey.getRelation()));
                }
            }
            return result;
        }
    };

    public static FilterInfoBeanFactory getInstance() {
        return SingletonHolder.factory;
    }

    private FilterInfoBeanFactory() {
    }

    private static class SingletonHolder {
        private static FilterInfoBeanFactory factory = new FilterInfoBeanFactory();
    }

    @Override
    public List<FilterInfoBean> create(List<FilterInfo> source) {
        List<FilterInfoBean> result = new ArrayList<FilterInfoBean>();
        if (null != source) {
            for (FilterInfo filterInfo : source) {
                result.add(SINGLE_FILTER_INFO_BEAN_FACTORY.create(filterInfo));
            }
        }
        return result;
    }
}
