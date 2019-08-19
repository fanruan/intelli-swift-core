package com.fr.swift.exception.event;

import com.fr.swift.event.base.AbstractExceptionRpcEvent;
import com.fr.swift.exception.ExceptionInfo;

/**
 * @author Marvin
 * @date 8/14/2019
 * @description
 * @since swift 1.1
 */
public class ExceptionStateRpcEvent extends AbstractExceptionRpcEvent {

    private static final long serialVersionUID = 2541169981911253167L;

    private ExceptionInfo info;

    public ExceptionStateRpcEvent(ExceptionInfo info) {
        this.info = info;
    }

    @Override
    public ExceptionInfo getContent() {
        return info;
    }

    @Override
    public Event subEvent() {
        if (info.getState() == ExceptionInfo.State.UNSOLVED) {
            return Event.UNSOLVED;
        } else {
            return Event.SOLVED;
        }
    }
}
