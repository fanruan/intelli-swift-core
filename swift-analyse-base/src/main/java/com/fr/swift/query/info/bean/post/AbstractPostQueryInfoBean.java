package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.type.PostQueryType;

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
