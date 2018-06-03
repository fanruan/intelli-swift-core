package com.fr.swift.query.info.bean;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class FilterInfoBean {

    @JsonProperty
    private SwiftDetailFilterType type;
}
