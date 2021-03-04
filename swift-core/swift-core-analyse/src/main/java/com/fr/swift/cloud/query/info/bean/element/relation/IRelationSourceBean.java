package com.fr.swift.cloud.query.info.bean.element.relation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.cloud.query.info.bean.element.relation.impl.FieldRelationSourceBean;
import com.fr.swift.cloud.query.info.bean.element.relation.impl.RelationPathSourceBean;
import com.fr.swift.cloud.query.info.bean.element.relation.impl.RelationSourceBean;
import com.fr.swift.cloud.source.RelationSourceType;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = RelationSourceType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelationPathSourceBean.class, name = "RELATION_PATH"),
        @JsonSubTypes.Type(value = FieldRelationSourceBean.class, name = "FIELD_RELATION"),
        @JsonSubTypes.Type(value = RelationSourceBean.class, name = "RELATION")
})
public interface IRelationSourceBean {
    RelationSourceType getType();

    String getPrimaryTable();


    String getForeignTable();


    List<String> getPrimaryFields();


    List<String> getForeignFields();

}
