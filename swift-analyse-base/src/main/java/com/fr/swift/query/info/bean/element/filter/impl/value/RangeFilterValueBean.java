package com.fr.swift.query.info.bean.element.filter.impl.value;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/7/4.
 */
public class RangeFilterValueBean implements Serializable {

    private static final long serialVersionUID = 1839806405052032454L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RangeFilterValueBean valueBean = (RangeFilterValueBean) o;

        if (startIncluded != valueBean.startIncluded) return false;
        if (endIncluded != valueBean.endIncluded) return false;
        if (start != null ? !start.equals(valueBean.start) : valueBean.start != null) return false;
        return end != null ? end.equals(valueBean.end) : valueBean.end == null;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (startIncluded ? 1 : 0);
        result = 31 * result + (endIncluded ? 1 : 0);
        return result;
    }
}
