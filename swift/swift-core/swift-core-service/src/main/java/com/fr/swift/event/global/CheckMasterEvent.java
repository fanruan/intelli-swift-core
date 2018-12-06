package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

import java.io.Serializable;

/**
 * This class created on 2018/11/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class CheckMasterEvent extends AbstractGlobalRpcEvent implements Serializable {
    private static final long serialVersionUID = 1640021465782197562L;

    @Override
    public Event subEvent() {
        return Event.CHECK_MASTER;
    }

    @Override
    public Object getContent() {
        return null;
    }
}
