package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.query.QueryInfo;

/**
 * @author yee
 * @date 2018/6/8
 */
public class HistoryQueryRpcEvent extends AbstractHistoryRpcEvent<QueryInfo> {
    private QueryInfo queryInfo;

    public HistoryQueryRpcEvent(QueryInfo queryInfo) {
        this.queryInfo = queryInfo;
    }


    @Override
    public Event subEvent() {
        return Event.QUERY;
    }

    @Override
    public QueryInfo getContent() {
        return queryInfo;
    }
}
