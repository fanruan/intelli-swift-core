package com.fr.swift.query.info.group.post;

import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.post.PostQueryType;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class CalculatedFieldQueryInfo implements PostQueryInfo {

    private List<GroupTarget> calInfoList;

    public CalculatedFieldQueryInfo(List<GroupTarget> calInfoList) {
        this.calInfoList = calInfoList;
    }

    public List<GroupTarget> getCalInfoList() {
        return calInfoList;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.CAL_FIELD;
    }
}
