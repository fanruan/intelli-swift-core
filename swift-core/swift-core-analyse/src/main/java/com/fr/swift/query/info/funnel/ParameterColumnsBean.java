package com.fr.swift.query.info.funnel;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * Created by lyon on 2018/12/28.
 */
public class ParameterColumnsBean {

    @JsonProperty
    private String id;
    @JsonProperty
    private String timestamp;

    public ParameterColumnsBean() {
    }

    public ParameterColumnsBean(String id, String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
