package com.fr.swift.query.info.group.post;

import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.info.element.target.GroupTarget;

/**
 * Created by Lyon on 2018/6/3.
 */
public class CalculatedFieldQueryInfo implements PostQueryInfo {

    private GroupTarget calInfo;

    public CalculatedFieldQueryInfo(GroupTarget calInfo) {
        this.calInfo = calInfo;
    }

    public GroupTarget getCalInfo() {
        return calInfo;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.CAL_FIELD;
    }
}
