package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventOrder;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2017/12/29
 */
public class WorkerTaskPool extends BaseTaskPool<WorkerTask> {
    private Function<Pair<TaskKey, Object>, WorkerTask> generator;

    public void setGenerator(Function<Pair<TaskKey, Object>, WorkerTask> generator) {
        this.generator = generator;
    }

    public void initListener() throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().addListener(new SwiftServiceListener<Collection<Pair<TaskKey, Object>>>() {
            @Override
            public void handle(SwiftServiceEvent<Collection<Pair<TaskKey, Object>>> event) {
                for (Pair<TaskKey, Object> pair : event.getContent()) {
                    add(generator.apply(pair));
                }
            }

            @Override
            public EventType getType() {
                return EventType.INIT_TASK;
            }

            @Override
            public EventOrder getOrder() {
                return EventOrder.AFTER;
            }
        });
        SwiftServiceListenerManager.getInstance().addListener(new SwiftServiceListener<TaskKey>() {
            @Override
            public void handle(SwiftServiceEvent<TaskKey> event) {
                WorkerTask workerTask = get(event.getContent());
                synchronized (workerTask) {
                    workerTask.onCancel();
                }
            }

            @Override
            public EventType getType() {
                return EventType.CANCEL_TASK;
            }

            @Override
            public EventOrder getOrder() {
                return EventOrder.AFTER;
            }
        });
    }

    private static final WorkerTaskPool INSTANCE = new WorkerTaskPool();

    private WorkerTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, WorkerTask>();
    }

    public static WorkerTaskPool getInstance() {
        return INSTANCE;
    }
}