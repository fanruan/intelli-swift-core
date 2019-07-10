package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
public class RelationPathSourceBean extends RelationSourceBean {

    @JsonProperty
    private List<IRelationSourceBean> relations;

    public RelationPathSourceBean() {
        super(RelationSourceType.RELATION_PATH);
    }

    public List<IRelationSourceBean> getRelations() {
        return relations;
    }

    public void setRelations(List<IRelationSourceBean> relations) {
        this.relations = relations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RelationPathSourceBean that = (RelationPathSourceBean) o;

        return relations != null ? relations.equals(that.relations) : that.relations == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        return result;
    }
}
