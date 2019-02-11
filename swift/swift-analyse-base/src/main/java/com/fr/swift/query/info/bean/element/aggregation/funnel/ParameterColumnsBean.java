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
    private String combine;

    public ParameterColumnsBean() {
    }

    public ParameterColumnsBean(String userId, String event, String combine) {
        this.userId = userId;
        this.event = event;
        this.combine = combine;
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

    public String getCombine() {
        return combine;
    }

    public void setCombine(String combine) {
        this.combine = combine;
    }
}
