package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/8/22
 */
public class GetAnalyseAndRealTimeAddrEvent extends AbstractGlobalRpcEvent<Void> implements Serializable {
    private static final long serialVersionUID = -7950417826376681408L;

    @Override
    public Event subEvent() {
        return Event.GET_ANALYSE_REAL_TIME;
    }

    @Override
    public Void getContent() {
        return null;
    }
}
