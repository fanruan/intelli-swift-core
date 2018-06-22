package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.bean.element.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.FilterInfoBean;
import com.fr.swift.query.info.bean.element.GeneralFilterInfoBean;
import com.fr.swift.query.info.bean.element.MatchFilterInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class FilterInfoBeanFactory implements BeanFactory<List<FilterInfo>, List<FilterInfoBean>> {

    public static final SingleFilterInfoBeanFactory SINGLE_FILTER_INFO_BEAN_FACTORY = new SingleFilterInfoBeanFactory();
    private static FilterInfoBeanFactory factory;

    private FilterInfoBeanFactory() {
    }

    public static FilterInfoBeanFactory getInstance() {
        if (null == factory) {
            synchronized (FilterInfoBeanFactory.class) {
                if (null == factory) {
                    factory = new FilterInfoBeanFactory();
                }
            }
        }
        return factory;
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

    public static class SingleFilterInfoBeanFactory implements BeanFactory<FilterInfo, FilterInfoBean> {

        @Override
        public FilterInfoBean create(FilterInfo source) {
            FilterInfoBean result = null;
            if (source instanceof GeneralFilterInfo) {
                result = new GeneralFilterInfoBean();
                ((GeneralFilterInfoBean) result).setChildren(FilterInfoBeanFactory.getInstance().create(((GeneralFilterInfo) source).getChildren()));
                ((GeneralFilterInfoBean) result).setType(((GeneralFilterInfo) source).getType());
            } else if (source instanceof DetailFilterInfoBean) {
                result = new DetailFilterInfoBean();
                ((DetailFilterInfoBean) result).setType(((DetailFilterInfoBean) source).getType());
                ((DetailFilterInfoBean) result).setFilterValue(((DetailFilterInfoBean) source).getFilterValue());
            } else {
                result = new MatchFilterInfoBean();
            }
            return result;
        }
    }
}
