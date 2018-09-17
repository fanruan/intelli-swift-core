package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractPostQueryInfoBean implements PostQueryInfoBean {

    @JsonProperty
    protected PostQueryType type;

    public abstract PostQueryType getType();

    public void setType(PostQueryType type) {
        this.type = type;
    }
}
