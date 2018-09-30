package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;

/**
 * @author yee
 * @date 2018/9/30
 */
public class CheckLoadHistoryEvent extends AbstractHistoryRpcEvent<Void> {
    private static final long serialVersionUID = -5945883684374975434L;

    @Override
    public Event subEvent() {
        return Event.CHECK_LOAD;
    }

    @Override
    public Void getContent() {
        return null;
    }
}
