package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

/**
 * @author yee
 * @date 2018/8/22
 */
public class GetAnalyseAndRealTimeAddrEvent extends AbstractGlobalRpcEvent<Void> {

    private static final long serialVersionUID = -4938142082438993520L;

    @Override
    public Event subEvent() {
        return Event.GET_ANALYSE_REAL_TIME;
    }

    @Override
    public Void getContent() {
        return null;
    }
}
