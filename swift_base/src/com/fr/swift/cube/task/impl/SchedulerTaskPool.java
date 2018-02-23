package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventOrder;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.structure.Pair;

import java.util.HashMap;

/**
 * @author anchore
 * @date 2017/12/15
 */
public class SchedulerTaskPool extends BaseTaskPool<SchedulerTask> {
    public void initListener() throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().addListener(new SwiftServiceListener<Pair<TaskKey, Result>>() {
            @Override
            public void handle(SwiftServiceEvent<Pair<TaskKey, Result>> event) {
                Pair<TaskKey, Result> pair = event.getContent();
                get(pair.key()).onDone(pair.value());
            }

            @Override
            public EventType getType() {
                return EventType.DONE_TASK;
            }

            @Override
            public EventOrder getOrder() {
                return EventOrder.AFTER;
            }
        });
    }

    private static final SchedulerTaskPool INSTANCE = new SchedulerTaskPool();

    private SchedulerTaskPool() {
        tasks = new HashMap<TaskKey, SchedulerTask>();
    }

    public static SchedulerTaskPool getInstance() {
        return INSTANCE;
    }
}