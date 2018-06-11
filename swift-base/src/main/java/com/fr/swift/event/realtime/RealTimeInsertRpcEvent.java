package com.fr.swift.event.realtime;

import com.fr.swift.event.base.AbstractRealTimeRpcEvent;

/**
 * @author yee
 * @date 2018/6/8
 */
public class RealTimeInsertRpcEvent extends AbstractRealTimeRpcEvent {
    private static final long serialVersionUID = 3406580520045812587L;

    @Override
    public Event subEvent() {
        return Event.INSERT;
    }

    @Override
    public Object getContent() {
        return null;
    }
}
