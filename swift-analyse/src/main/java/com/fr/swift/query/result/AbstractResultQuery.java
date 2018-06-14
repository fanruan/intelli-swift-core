package com.fr.swift.query.result;

import com.fr.swift.query.query.Query;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * Created by pony on 2017/11/27.
 * 处理结果的query
 */
public abstract class AbstractResultQuery<T extends SwiftResultSet> implements ResultQuery<T> {
    /**
     * ResultQuery是由多个query合并来的
     */
    protected List<Query<T>> queryList;


    public AbstractResultQuery(List<Query<T>> queryList) {
        this.queryList = queryList;
    }

}
