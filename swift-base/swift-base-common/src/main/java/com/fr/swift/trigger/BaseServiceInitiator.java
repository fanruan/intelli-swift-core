package com.fr.swift.trigger;

import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2020/5/7
 *
 * @author Kuifang.Liu
 */
public abstract class BaseServiceInitiator<D> {
    private List<SwiftPriorityInitTrigger> TRIGGERS;

    public BaseServiceInitiator(List<SwiftPriorityInitTrigger> triggers) {
        TRIGGERS = triggers;
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

    public void initByPriority(D data) {
        synchronized (TRIGGERS) {
            List<SwiftPriorityInitTrigger> triggers = new ArrayList<>(TRIGGERS);
            triggers.sort((t1, t2) -> Integer.compare(t2.priority(), t1.priority()));
            triggers.forEach(trigger -> {
                try {
                    trigger.trigger(data);
                } catch (Exception e) {
                    Crasher.crash(e);
                }
            });
        }
    }
}
