package com.fr.swift.exception.event;

import com.fr.swift.event.base.AbstractHealthInspectionRpcEvent;
import com.fr.swift.service.SwiftService;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class ServiceHealthInspectionRpcEvent extends AbstractHealthInspectionRpcEvent<SwiftService> {
    private static final long serialVersionUID = 6914730085135484899L;

    private Event event;
    private SwiftService service;

    public ServiceHealthInspectionRpcEvent inspectMasterAccessiable() {
        this.event = Event.INSPECT_MASTER;
        return this;
    }

    public ServiceHealthInspectionRpcEvent inspectOtherSlaveAccessiable(SwiftService service) {
        this.event = Event.INSPECT_SLAVE;
        this.service = service;
        return this;
    }

    @Override
    public SwiftService getContent() {
        return service;
    }

    @Override
    public Event subEvent() {
        return event;
    }
}
