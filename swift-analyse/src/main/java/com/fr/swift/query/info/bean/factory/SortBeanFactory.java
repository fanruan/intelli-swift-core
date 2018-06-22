package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.sort.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class SortBeanFactory implements BeanFactory<List<Sort>, List<SortBean>> {

    public static final SingleSortBeanFactory SINGLE_SORT_BEAN_FACTORY = new SingleSortBeanFactory();

    @Override
    public List<SortBean> create(List<Sort> source) {
        List<SortBean> result = new ArrayList<SortBean>();
        if (null != source) {
            for (Sort sort : source) {
                result.add(SINGLE_SORT_BEAN_FACTORY.create(sort));
            }
        }
        return result;
    }

    public static class SingleSortBeanFactory implements BeanFactory<Sort, SortBean> {

        private SingleSortBeanFactory() {
        }

        @Override
        public SortBean create(Sort source) {
            SortBean bean = new SortBean();
            bean.setType(source.getSortType());
            bean.setColumnKey(source.getColumnKey());
            bean.setTargetIndex(source.getTargetIndex());
            return bean;
        }
    }
}
