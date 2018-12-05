package com.fr.swift.task.impl;

import com.fr.swift.event.SwiftEvent;

/**
 * @author anchore
 * @date 2018/6/26
 */
public enum TaskEvent implements SwiftEvent {
    //
    TRIGGER,
    RUN,
    LOCAL_RUN,
    CANCEL,
    LOCAL_CANCEL,
    DONE, LOCAL_DONE
}