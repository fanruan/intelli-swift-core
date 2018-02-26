package com.fr.swift.service;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.minor.MinorSegmentManager;
import com.fr.swift.manager.LocalSegmentOperatorProvider;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SingleTypeListenerContainer;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pony
 * @date 2017/10/10
 */
public abstract class AbstractSwiftServerService extends AbstractSwiftService implements SwiftServiceListenerHandler {

    private Map<EventType, SingleTypeListenerContainer> listenerMap = new ConcurrentHashMap<EventType, SingleTypeListenerContainer>();

    @PostConstruct
    @Override
    public boolean start() {
        FRContext.setCurrentEnv(new LocalEnv(new File(AbstractSwiftServerService.class.getResource("/").getPath()).getParent()));

        initListener();
        SwiftServiceListenerManager.getInstance().registerHandler(this);
        SwiftContext.getInstance().registerSegmentProvider(LocalSegmentProvider.getInstance());
        SwiftContext.getInstance().registerSegmentOperatorProvider(LocalSegmentOperatorProvider.getInstance());
        SwiftContext.getInstance().registerMinorSegmentManager(MinorSegmentManager.getInstance());
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SERVER;
    }

    @Override
    public void trigger(SwiftServiceEvent event) {
        synchronized (this) {
            SingleTypeListenerContainer container = listenerMap.get(event.getEventType());
            if (container != null) {
                for (List<SwiftServiceListener> listenerList : container) {
                    for (SwiftServiceListener listener : listenerList) {
                        listener.handle(event);
                    }
                }
            }
        }
    }

    protected void initListener() {
        for (EventType type : EventType.values()) {
            listenerMap.put(type, new SingleTypeListenerContainer());
        }
    }


    @Override
    public void addListener(SwiftServiceListener listener) {
        listenerMap.get(listener.getType()).addListener(listener);
    }
}
