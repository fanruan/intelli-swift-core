package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class SortBeanFactory implements BeanFactory<List<Sort>, List<SortBean>> {

    public static final BeanFactory<Sort, SortBean> SINGLE_SORT_BEAN_FACTORY = new BeanFactory<Sort, SortBean>() {
        @Override
        public SortBean create(Sort source) {
            SortBean bean = new SortBean();
            bean.setType(source.getSortType());
            ColumnKey columnKey = source.getColumnKey();
            if (null != columnKey) {
                bean.setColumn(columnKey.getName());
                bean.setRelation(RelationSourceBeanFactory.SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(columnKey.getRelation()));
            }
            return bean;
        }
    };

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

    private SortBeanFactory() {
    }

    public static SortBeanFactory getInstance() {
        return SingletonHolder.factory;
    }

    private static class SingletonHolder {
        private static SortBeanFactory factory = new SortBeanFactory();
    }
}
