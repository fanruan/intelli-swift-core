package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.sort.SortType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private String fieldName;

    public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
