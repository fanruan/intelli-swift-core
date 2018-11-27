package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.info.bean.type.DimensionType;
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
    private String alias;    // 客户端定义的转移名
    @JsonProperty
    private SortBean sortBean;
    @JsonProperty
    private DimensionType dimensionType;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public SortBean getSortBean() {
        return sortBean;
    }

    public void setSortBean(SortBean sortBean) {
        this.sortBean = sortBean;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(DimensionType dimensionType) {
        this.dimensionType = dimensionType;
    }
}
