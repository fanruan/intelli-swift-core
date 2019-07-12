package com.fr.swift.query.info.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * Created by lyon on 2018/12/28.
 */
public class ParameterColumnsBean {

    @JsonProperty
    private String userId;
    @JsonProperty
    private String timestamp;

    public ParameterColumnsBean() {
    }

    public ParameterColumnsBean(String userId, String timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
