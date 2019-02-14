package com.fr.swift.query.result;

import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * Created by pony on 2017/11/27.
 * 处理结果的query
 */
public abstract class AbstractResultQuery<T extends QueryResultSet> implements ResultQuery<T> {

    protected int fetchSize;
    /**
     * ResultQuery是由多个query合并来的
     */
    protected List<Query<T>> queryList;

    public AbstractResultQuery(int fetchSize, List<Query<T>> queryList) {
        this.fetchSize = fetchSize;
        this.queryList = queryList;
    }

}
