package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.type.DimensionType;


/**
 *
 * @author Lyon
 * @date 2018/6/2
 */
public class DimensionBean {
    /**
     * 原始表中的字段名
     */
    @JsonProperty
    private String column;
    /**
     * 客户端定义的转译名
     */
    @JsonProperty
    private String alias;
    /**
     * 排序信息
     */
    @JsonProperty
    private SortBean sortBean;
    /**
     * 字段类型
     */
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
