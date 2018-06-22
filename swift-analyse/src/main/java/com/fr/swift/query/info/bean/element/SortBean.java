package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private ColumnKey columnKey;
    @JsonProperty
    private int targetIndex;

    public SortBean(SortType type, ColumnKey columnKey, int targetIndex) {
        this.type = type;
        this.columnKey = columnKey;
        this.targetIndex = targetIndex;
    }

    public SortBean() {
    }

    public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
        this.type = type;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(ColumnKey columnKey) {
        this.columnKey = columnKey;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
}
