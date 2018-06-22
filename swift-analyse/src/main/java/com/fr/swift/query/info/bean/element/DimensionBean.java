package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class DimensionBean {

    @JsonProperty
    private SourceKey sourceKey;
    @JsonProperty
    private ColumnKey columnKey;
    @JsonProperty
    private GroupBean groupBean;
    @JsonProperty
    private SortBean sortBean;
    @JsonProperty
    private FilterInfoBean filterInfoBean;
    @JsonProperty
    private String formula;
    @JsonProperty
    private Dimension.DimensionType dimensionType;

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

    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }

    public void setFilterInfoBean(FilterInfoBean filterInfoBean) {
        this.filterInfoBean = filterInfoBean;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public SourceKey getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(SourceKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(ColumnKey columnKey) {
        this.columnKey = columnKey;
    }

    public Dimension.DimensionType getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(Dimension.DimensionType dimensionType) {
        this.dimensionType = dimensionType;
    }
}
