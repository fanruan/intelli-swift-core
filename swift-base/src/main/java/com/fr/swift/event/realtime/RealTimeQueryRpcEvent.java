package com.fr.swift.event.realtime;

import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.query.QueryInfo;

/**
 * @author yee
 * @date 2018/6/8
 */
public class RealTimeQueryRpcEvent extends AbstractRealTimeRpcEvent<QueryInfo> {

    private static final long serialVersionUID = 7874082264391942343L;
    private QueryInfo queryInfo;

    public RealTimeQueryRpcEvent(QueryInfo queryInfo) {
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
