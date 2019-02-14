package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.FieldRelationSourceBean;
import com.fr.swift.query.info.bean.element.relation.impl.RelationPathSourceBean;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.source.relation.RelationSourceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
class RelationSourceParser {
    public static RelationSource parse(IRelationSourceBean bean) {
        if (null != bean) {
            switch (bean.getType()) {
                case RELATION:
                    return new RelationSourceImpl(new SourceKey(bean.getPrimaryTable()),
                            new SourceKey(bean.getForeignTable()), bean.getPrimaryFields(), bean.getForeignFields());
                case RELATION_PATH:
                    return new RelationPathSourceImpl(parse(((RelationPathSourceBean) bean).getRelations()));
                case FIELD_RELATION:
                    FieldRelationSourceBean fieldBean = (FieldRelationSourceBean) bean;
                    ColumnKey columnKey = new ColumnKey(fieldBean.getColumnName());
                    columnKey.setRelation(parse(fieldBean.getColumnRelation()));
                    return new FieldRelationSource(columnKey);
            }
        }
        return null;
    }

    static List<RelationSource> parse(List<IRelationSourceBean> beans) {
        List<RelationSource> result = new ArrayList<RelationSource>();
        if (null != beans) {
            for (IRelationSourceBean bean : beans) {
                result.add(parse(bean));
            }
        }
        return result;
    }
}
