package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.type.DimensionType;


/**
 * Created by Lyon on 2018/6/2.
 */
public class DimensionBean {

    @JsonProperty
    private String column;  // 原始表中的字段名
    @JsonProperty
    private String alias;    // 客户端定义的转移名
    @JsonProperty
    private SortBean sortBean;
    @JsonProperty
    private DimensionType type;

    public DimensionBean() {
    }

    public DimensionBean(DimensionType type) {
        this.type = type;
    }

    public DimensionBean(DimensionType type, String column) {
        this.column = column;
        this.type = type;
    }

    public DimensionBean(DimensionType type, String column, String alias) {
        this.column = column;
        this.type = type;
        this.alias = alias;
    }

    public DimensionBean(DimensionType type, String column, SortBean sortBean) {
        this.column = column;
        this.sortBean = sortBean;
        this.type = type;
    }

    public DimensionBean(DimensionType type, String column, String alias, SortBean sortBean) {
        this.column = column;
        this.alias = alias;
        this.sortBean = sortBean;
        this.type = type;
    }

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

    public DimensionType getType() {
        return type;
    }

    public void setType(DimensionType type) {
        this.type = type;
    }
}
