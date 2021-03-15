package com.fr.swift.cloud.trigger;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public interface SwiftPriorityInitTrigger<E extends TriggerEvent> {

    default void trigger(E e) throws Exception {
        if (e == TriggerEvent.INIT) {
            init();
        } else {
            destroy();
        }
    }

    void init() throws Exception;

    void destroy() throws Exception;

    int priority();

    enum Priority {
        LOWER(10), LOW(20), MEDIAN(30), HIGH(40), HIGHER(50);

        private int priority;

        Priority(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }
    }
}
