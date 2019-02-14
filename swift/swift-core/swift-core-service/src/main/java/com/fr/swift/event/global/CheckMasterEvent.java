package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

/**
 * This class created on 2018/11/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class CheckMasterEvent extends AbstractGlobalRpcEvent {

    private static final long serialVersionUID = 6500392706441900658L;

    @Override
    public Event subEvent() {
        return Event.CHECK_MASTER;
    }

    @Override
    public Object getContent() {
        return null;
    }
}
