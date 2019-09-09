package com.fr.swift.exception.event;

import com.fr.swift.event.base.AbstractHealthInspectionRpcEvent;
import com.fr.swift.exception.inspect.bean.ComponentHealthInfo;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class ServiceHealthInspectionRpcEvent extends AbstractHealthInspectionRpcEvent<ComponentHealthInfo> {
    private static final long serialVersionUID = 6914730085135484899L;

    private Event event;
    private ComponentHealthInfo info;

    public ServiceHealthInspectionRpcEvent inspectOnlyMaster() {
        this.event = Event.INSPECT_MASTER;
        return this;
    }

    public ServiceHealthInspectionRpcEvent inspectOtherSlavesOverMaster(ComponentHealthInfo info) {
        this.event = Event.INSPECT_SLAVE;
        this.info = info;
        return this;
    }

    @Override
    public ComponentHealthInfo getContent() {
        return info;
    }

    @Override
    public Event subEvent() {
        return event;
    }
}
