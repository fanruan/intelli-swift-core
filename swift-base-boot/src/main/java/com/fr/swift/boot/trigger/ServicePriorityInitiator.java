package com.fr.swift.boot.trigger;

import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public class ServicePriorityInitiator {

    private static List<SwiftPriorityInitTrigger> TRIGGERS = new ArrayList<>();


    public static void register(SwiftPriorityInitTrigger trigger) {
        synchronized (TRIGGERS) {
            TRIGGERS.add(trigger);
        }
    }

    public static void unregister(SwiftPriorityInitTrigger trigger) {
        synchronized (TRIGGERS) {
            TRIGGERS.remove(trigger);
        }
    }

    public static void initByPrioriry() {
        synchronized (TRIGGERS) {
            List<SwiftPriorityInitTrigger> triggers = new ArrayList<>(TRIGGERS);
            triggers.sort((t1, t2) -> Integer.compare(t2.priority(), t1.priority()));
            triggers.forEach(trigger -> {
                try {
                    trigger.trigger(null);
                } catch (Exception e) {
                    Crasher.crash(e);
                }
            });
        }
    }
}
