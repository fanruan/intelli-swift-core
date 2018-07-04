package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by Lyon on 2018/6/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionBean {

    @JsonProperty
    private String column;  // 原始表中的字段名
    @JsonProperty
    private String name;    // 客户端定义的转移名
    @JsonProperty
    private GroupBean groupBean;
    @JsonProperty
    private SortBean sortBean;
    @JsonProperty
    private String formula;
    @JsonProperty
    private Dimension.DimensionType dimensionType;
    @JsonProperty
    private IRelationSourceBean relation;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupBean getGroupBean() {
        return groupBean;
    }

    public void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }

    public SortBean getSortBean() {
        return sortBean;
    }

    public void setSortBean(SortBean sortBean) {
        this.sortBean = sortBean;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Dimension.DimensionType getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(Dimension.DimensionType dimensionType) {
        this.dimensionType = dimensionType;
    }

    public IRelationSourceBean getRelation() {
        return relation;
    }

    public void setRelation(IRelationSourceBean relation) {
        this.relation = relation;
    }
}
