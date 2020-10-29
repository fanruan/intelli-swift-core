package com.fr.swift.trigger;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class created on 2020/5/7
 *
 * @author Kuifang.Liu
 */
public abstract class BaseServiceInitiator<E extends TriggerEvent> {
    private final List<SwiftPriorityInitTrigger> TRIGGERS;

    private final AtomicBoolean started;

    public BaseServiceInitiator(List<SwiftPriorityInitTrigger> triggers) {
        TRIGGERS = triggers;
        started = new AtomicBoolean();
    }

    public void register(SwiftPriorityInitTrigger trigger) {
        synchronized (TRIGGERS) {
            TRIGGERS.add(trigger);
        }
    }

    public void unregister(SwiftPriorityInitTrigger trigger) {
        synchronized (TRIGGERS) {
            TRIGGERS.remove(trigger);
        }
    }

    public void triggerByPriority(E event) {
        if (event == TriggerEvent.INIT) {
            if (started.getAndSet(true)) {
                SwiftLoggers.getLogger().info("{} has been inited", this.getClass().getName());
                return;
            }
        } else {
            if (!started.getAndSet(false)) {
                SwiftLoggers.getLogger().info("{} has been destroyed", this.getClass().getName());
                return;
            }
        }
        synchronized (TRIGGERS) {
            List<SwiftPriorityInitTrigger> triggers = new ArrayList<>(TRIGGERS);
            if (event == TriggerEvent.INIT) {
                triggers.sort((t1, t2) -> Integer.compare(t2.priority(), t1.priority()));
            } else {
                triggers.sort(Comparator.comparingInt(SwiftPriorityInitTrigger::priority));
            }
            triggers.forEach(trigger -> {
                try {
                    trigger.trigger(event);
                } catch (Exception e) {
                    Crasher.crash(e);
                }
            });
        }
    }
}
