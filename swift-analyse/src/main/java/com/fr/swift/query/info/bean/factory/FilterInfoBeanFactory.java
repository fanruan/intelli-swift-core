package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.GeneralFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.MatchFilterInfoBean;
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
            FilterInfoBean result = null;
            if (source instanceof GeneralFilterInfo) {
                result = new GeneralFilterInfoBean();
                ((GeneralFilterInfoBean) result).setChildren(FilterInfoBeanFactory.getInstance().create(((GeneralFilterInfo) source).getChildren()));
                ((GeneralFilterInfoBean) result).setType(((GeneralFilterInfo) source).getType());
            } else if (source instanceof SwiftDetailFilterInfo) {
                result = new DetailFilterInfoBean();
                ((DetailFilterInfoBean) result).setType(((SwiftDetailFilterInfo) source).getType());
                ((DetailFilterInfoBean) result).setFilterValue(((SwiftDetailFilterInfo) source).getFilterValue());
                ColumnKey columnKey = ((SwiftDetailFilterInfo) source).getColumnKey();
                if (null != columnKey) {
                    ((DetailFilterInfoBean) result).setColumn(columnKey.getName());
                    ((DetailFilterInfoBean) result).setRelation(RelationSourceBeanFactory.SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(columnKey.getRelation()));
                }
            } else {
                // TODO MatchFilterInfo 属性
                result = new MatchFilterInfoBean();
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
