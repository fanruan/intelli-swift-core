package com.fr.swift.query.info.bean.element.aggregation.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * Created by lyon on 2018/12/28.
 */
public class ParameterColumnsBean {

    @JsonProperty
    private String userId;
    @JsonProperty
    private String event;
    @JsonProperty
    private String timestamp;
    @JsonProperty
    private String date;

    public ParameterColumnsBean() {
    }

    public ParameterColumnsBean(String userId, String event, String timestamp, String date) {
        this.userId = userId;
        this.event = event;
        this.timestamp = timestamp;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
