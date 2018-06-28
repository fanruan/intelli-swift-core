package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.FieldRelationSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.RelationPathSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.RelationSourceBean;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.source.relation.RelationPathSourceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
public class RelationSourceBeanFactory implements BeanFactory<List<RelationSource>, List<IRelationSourceBean>> {

    public static final BeanFactory<RelationSource, IRelationSourceBean> SINGLE_RELATION_SOURCE_BEAN_FACTORY = new BeanFactory<RelationSource, IRelationSourceBean>() {
        @Override
        public IRelationSourceBean create(RelationSource source) {
            if (null != source) {
                switch (source.getRelationType()) {
                    case FIELD_RELATION:
                        FieldRelationSourceBean fieldBean = new FieldRelationSourceBean();
                        ColumnKey columnKey = ((FieldRelationSource) source).getColumnKey();
                        fieldBean.setColumnName(columnKey.getName());
                        fieldBean.setColumnRelation(SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(columnKey.getRelation()));
                        return fieldBean;
                    case RELATION_PATH:
                        RelationPathSourceBean pathBean = new RelationPathSourceBean();
                        pathBean.setRelations(getInstance().create(((RelationPathSourceImpl) source).getRelations()));
                        return pathBean;
                    case RELATION:
                        RelationSourceBean relationBean = new RelationSourceBean();
                        relationBean.setPrimaryTable(source.getPrimarySource().getId());
                        relationBean.setPrimaryFields(source.getPrimaryFields());
                        relationBean.setForeignTable(source.getForeignSource().getId());
                        relationBean.setForeignFields(source.getForeignFields());
                        return relationBean;
                }
            }
            return null;
        }
    };

    private RelationSourceBeanFactory() {
    }

    private static RelationSourceBeanFactory getInstance() {
        return SingletonHolder.factory;
    }

    private static class SingletonHolder {
        private static RelationSourceBeanFactory factory = new RelationSourceBeanFactory();
    }

    @Override
    public List<IRelationSourceBean> create(List<RelationSource> source) {
        List<IRelationSourceBean> result = new ArrayList<IRelationSourceBean>();

        if (null != source) {
            for (RelationSource relationSource : source) {
                result.add(SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(relationSource));
            }
        }
        return result;
    }
}
