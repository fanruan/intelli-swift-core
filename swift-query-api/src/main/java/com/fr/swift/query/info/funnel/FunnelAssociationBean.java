package com.fr.swift.query.info.funnel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class FunnelAssociationBean {

    @JsonProperty("columns")
    private List<String> columns;
    @JsonProperty("events")
    private List<Integer> events;

    public FunnelAssociationBean() {
    }

    public FunnelAssociationBean(List<String> columns, List<Integer> events) {
        this.columns = columns;
        this.events = events;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Integer> getEvents() {
        return events;
    }

    public void setEvents(List<Integer> events) {
        this.events = events;
    }
}
