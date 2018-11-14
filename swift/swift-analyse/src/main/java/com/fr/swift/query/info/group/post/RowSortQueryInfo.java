package com.fr.swift.query.info.group.post;

import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.sort.Sort;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class RowSortQueryInfo implements PostQueryInfo {

    private List<Sort> sortList;

    public RowSortQueryInfo(List<Sort> sortList) {
        this.sortList = sortList;
    }

    public List<Sort> getSortList() {
        return sortList;
    }

    @Override
    public PostQueryType getType() {
        return PostQueryType.ROW_SORT;
    }
}
