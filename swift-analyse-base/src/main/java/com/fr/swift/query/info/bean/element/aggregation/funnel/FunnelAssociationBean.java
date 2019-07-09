package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelAssociationBean {

    @JsonProperty("column")
    private String column;
    @JsonProperty("events")
    private List<Integer> events;

    public FunnelAssociationBean() {
    }

    public FunnelAssociationBean(String column, List<Integer> events) {
        this.column = column;
        this.events = events;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<Integer> getEvents() {
        return events;
    }

    public void setEvents(List<Integer> events) {
        this.events = events;
    }
}