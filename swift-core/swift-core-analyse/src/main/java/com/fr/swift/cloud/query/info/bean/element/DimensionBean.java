package com.fr.swift.cloud.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.query.info.bean.type.DimensionType;


/**
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
    @JsonProperty
    private FormulaBean formula;

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

    public FormulaBean getFormula() {
        return formula;
    }

    public void setFormula(FormulaBean formula) {
        this.formula = formula;
    }

//    public static List<DimensionBean> ofDeatailList(List<Pair<String, String>> pairs) {
//        return pairs.stream().map(pair -> ofDetail(pair.getKey(), pair.getValue())).collect(Collectors.toList());
//    }

    public static DimensionBean ofGroup(String column) {
        return new Builder().setType(DimensionType.GROUP).setColumn(column).build();
    }

    public static DimensionBean ofGroup(String column, String alias) {
        return new Builder().setType(DimensionType.GROUP).setColumn(column).setAlias(alias).build();
    }

    public static DimensionBean ofDetail(String column) {
        return new Builder().setType(DimensionType.DETAIL).setColumn(column).build();
    }

    public static DimensionBean ofDetail(String column, String alias) {
        return new Builder().setType(DimensionType.DETAIL).setColumn(column).setAlias(alias).build();
    }

    public static class Builder {

        private DimensionBean dimensionBean = new DimensionBean();

        public Builder() {
        }

        public Builder setType(DimensionType type) {
            dimensionBean.type = type;
            return this;
        }

        public Builder setColumn(String column) {
            dimensionBean.column = column;
            return this;
        }

        public Builder setAlias(String alias) {
            dimensionBean.alias = alias;
            return this;
        }

        public DimensionBean build() {
            return dimensionBean;
        }

    }
}
