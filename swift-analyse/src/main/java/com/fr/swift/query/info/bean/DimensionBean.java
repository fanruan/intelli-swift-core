package com.fr.swift.query.info.bean;

import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class DimensionBean {

    @JsonProperty
    private String name;
    @JsonProperty
    private SortBean sortBean;
    @JsonProperty
    private GroupBean groupBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortBean getSortBean() {
        return sortBean;
    }

    public void setSortBean(SortBean sortBean) {
        this.sortBean = sortBean;
    }

    public GroupBean getGroupBean() {
        return groupBean;
    }

    public void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }
}
