package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2018/11/20
 */
public class SelectSqlInfo extends BaseSqlInfo {
    @JsonProperty("where")
    private String where;
    @JsonProperty("order")
    private List<Order> order;
    @JsonProperty("columns")
    private List<String> selectColumns;
    @JsonProperty("groupBy")
    private List<String> group;

    public SelectSqlInfo(String sql, String authCode) {
        super(sql, authCode);
        setRequest(Request.SELECT);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public List<String> getGroup() {
        return group;
    }

    public void setGroup(List<String> group) {
        this.group = group;
    }
}
