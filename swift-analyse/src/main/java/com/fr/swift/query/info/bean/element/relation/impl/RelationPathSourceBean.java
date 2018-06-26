package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
}
