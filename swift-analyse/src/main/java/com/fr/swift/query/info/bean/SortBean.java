package com.fr.swift.query.info.bean;

import com.fr.swift.query.sort.SortType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private String sortedColumn;
    @JsonProperty
    private String sortedByColumn;

    public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
        this.type = type;
    }

    public String getSortedColumn() {
        return sortedColumn;
    }

    public void setSortedColumn(String sortedColumn) {
        this.sortedColumn = sortedColumn;
    }

    public String getSortedByColumn() {
        return sortedByColumn;
    }

    public void setSortedByColumn(String sortedByColumn) {
        this.sortedByColumn = sortedByColumn;
    }
}
