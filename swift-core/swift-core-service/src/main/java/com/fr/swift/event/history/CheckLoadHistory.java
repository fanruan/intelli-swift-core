package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;

/**
 * @author yee
 * @date 2018/9/30
 */
public class CheckLoadHistory extends AbstractHistoryRpcEvent<Void> {
    @Override
    public Event subEvent() {
        return Event.CHECK_LOAD;
    }

    @Override
    public Void getContent() {
        return null;
    }
}
