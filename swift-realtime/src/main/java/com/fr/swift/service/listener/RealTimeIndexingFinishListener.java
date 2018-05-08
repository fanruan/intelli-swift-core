package com.fr.swift.service.listener;

import com.fr.swift.stuff.RealTimeIndexingStuff;

/**
 * Created by pony on 2017/11/6.
 */
public abstract class RealTimeIndexingFinishListener extends AbstractSwiftServiceListener<RealTimeIndexingStuff>{

    @Override
    public EventType getType() {
        return EventType.REAL_TIME_INDEX_FINISH;
    }

    @Override
    public EventOrder getOrder() {
        return EventOrder.AFTER;
    }
}
