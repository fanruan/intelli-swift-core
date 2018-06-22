package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
// TODO: 2018/6/8
public class DetailFilterInfoBean implements FilterInfoBean {

    @JsonProperty
    private SwiftDetailFilterType type;

    @JsonProperty
    private Object filterValue;

    public SwiftDetailFilterType getType() {
        return type;
    }

    public void setType(SwiftDetailFilterType type) {
        this.type = type;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }
}
