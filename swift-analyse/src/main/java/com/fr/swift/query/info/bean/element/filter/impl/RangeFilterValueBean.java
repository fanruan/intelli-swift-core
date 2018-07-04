package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/7/4.
 */
public class RangeFilterValueBean {

    @JsonProperty
    private String start;
    @JsonProperty
    private String end;
    @JsonProperty
    private boolean startIncluded = false;
    @JsonProperty
    private boolean endIncluded = false;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isStartIncluded() {
        return startIncluded;
    }

    public void setStartIncluded(boolean startIncluded) {
        this.startIncluded = startIncluded;
    }

    public boolean isEndIncluded() {
        return endIncluded;
    }

    public void setEndIncluded(boolean endIncluded) {
        this.endIncluded = endIncluded;
    }
}
