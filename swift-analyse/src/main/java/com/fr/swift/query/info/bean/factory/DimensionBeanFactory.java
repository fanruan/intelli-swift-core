package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.element.dimension.AbstractDimension;
import com.fr.swift.query.info.element.dimension.DetailFormulaDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.GroupFormulaDimension;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class DimensionBeanFactory implements BeanFactory<List<Dimension>, List<DimensionBean>> {

    public static final BeanFactory<Dimension, DimensionBean> SINGLE_DIMENSION_BEAN_FACTORY = new BeanFactory<Dimension, DimensionBean>() {
        @Override
        public DimensionBean create(Dimension source) {
            Group group = source.getGroup();
            Sort sort = source.getSort();
            AbstractDimension dimension = (AbstractDimension) source;
            FilterInfo filterInfo = dimension.getFilter();
            DimensionBean bean = new DimensionBean();
            if (null != filterInfo) {
                bean.setFilterInfoBean(FilterInfoBeanFactory.SINGLE_FILTER_INFO_BEAN_FACTORY.create(filterInfo));
            }
            SourceKey sourceKey = dimension.getSourceKey();
            ColumnKey columnKey = dimension.getColumnKey();
            bean.setDimensionType(source.getDimensionType());
            bean.setTable(sourceKey.getId());
            if (null != columnKey) {
                bean.setColumn(columnKey.getName());
                RelationSource relationSource = columnKey.getRelation();
                bean.setRelation(RelationSourceBeanFactory.SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(relationSource));
            }
            if (null != group) {
                GroupBean groupBean = new GroupBean();
                groupBean.setType(group.getGroupType());
                bean.setGroupBean(groupBean);
            }
            if (null != sort) {
                bean.setSortBean(SortBeanFactory.SINGLE_SORT_BEAN_FACTORY.create(sort));
            }
            switch (source.getDimensionType()) {
                case DETAIL_FORMULA:
                    bean.setFormula(((DetailFormulaDimension) dimension).getFormula());
                    break;
                case GROUP_FORMULA:
                    bean.setFormula(((GroupFormulaDimension) dimension).getFormula());
                    break;
            }
            return bean;
        }
    };

    @Override
    public List<DimensionBean> create(List<Dimension> dimensionList) {
        List<DimensionBean> result = new ArrayList<DimensionBean>();
        if (null != dimensionList) {
            for (Dimension item : dimensionList) {
                result.add(SINGLE_DIMENSION_BEAN_FACTORY.create(item));
            }
        }
        return result;
    }

    private DimensionBeanFactory() {
    }

    public static DimensionBeanFactory getInstance() {
        return SingletonHolder.factory;
    }

    private static class SingletonHolder {
        private static final DimensionBeanFactory factory = new DimensionBeanFactory();
    }
}
