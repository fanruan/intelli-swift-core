package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.RemoveSegLocationRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.listener.RemoteSender;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2018/12/28
 */
public class RemoveSegmentLocationListener extends BaseSegmentLocationListener {

    @Override
    Serializable trigger(SegmentLocationInfo segLocations) {
        try {
            return ProxySelector.getProxy(RemoteSender.class).trigger(new RemoveSegLocationRpcEvent(SwiftProperty.getProperty().getClusterId(), segLocations));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    static {
        SwiftEventDispatcher.listen(SyncSegmentLocationEvent.REMOVE_SEG, new RemoveSegmentLocationListener());
    }

    private RemoveSegmentLocationListener() {
    }

    public static void listen() {
    }
}