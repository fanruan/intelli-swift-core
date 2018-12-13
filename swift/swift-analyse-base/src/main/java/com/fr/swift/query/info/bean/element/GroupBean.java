package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.group.GroupType;

/**
 * Created by Lyon on 2018/6/2.
 */
public class GroupBean {

    @JsonProperty
    private GroupType type;

    // TODO: 2018/6/2 仅通过type能确定groupOperator吗？

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }
}
