package com.fr.swift.event;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Assert;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * @author anchore
 * @date 12/4/2018
 */
public class SwiftEventDispatcher {

    private static final ConcurrentMap<SwiftEvent, List<SwiftEventListener>> EVENTS = new ConcurrentHashMap<SwiftEvent, List<SwiftEventListener>>();

    private static final ExecutorService EXEC = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(SwiftEventDispatcher.class));

    public static void listen(SwiftEvent event, SwiftEventListener<?> listener) {
        Assert.notNull(event);
        Assert.notNull(listener);

        synchronized (EVENTS) {
            if (EVENTS.containsKey(event)) {
                EVENTS.get(event).add(listener);
            } else {
                List<SwiftEventListener> listeners = new ArrayList<SwiftEventListener>(Collections.singleton(listener));
                EVENTS.put(event, listeners);
            }
        }
    }

    public static void remove(SwiftEventListener<?> listener) {
        if (listener == null) {
            return;
        }

        synchronized (EVENTS) {
            for (List<SwiftEventListener> listeners : EVENTS.values()) {
                Iterator<SwiftEventListener> itr = listeners.iterator();
                while (itr.hasNext()) {
                    if (itr.next() == listener) {
                        itr.remove();
                    }
                }
            }
        }
    }

    public static <T> void fire(final SwiftEvent event, final T content) {
        Assert.notNull(event);

        EXEC.execute(new Runnable() {
            @Override
            public void run() {
                syncFire(event, content);
            }
        });
    }

    public static void fire(SwiftEvent event) {
        fire(event, null);
    }

    public static <T> void syncFire(final SwiftEvent event, final T content) {
        Assert.notNull(event);

        synchronized (EVENTS) {
            if (EVENTS.containsKey(event)) {
                for (SwiftEventListener listener : EVENTS.get(event)) {
                    try {
                        listener.on(content);
                    } catch (Throwable t) {
                        SwiftLoggers.getLogger().warn(t);
                    }
                }
            } else {
                // warn
                SwiftLoggers.getLogger().warn("no listener for event {}", event);
            }
        }
    }

    public static void syncFire(SwiftEvent event) {
        syncFire(event, null);
    }
}