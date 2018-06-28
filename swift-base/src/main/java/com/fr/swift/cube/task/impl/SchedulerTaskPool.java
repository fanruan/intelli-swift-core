package com.fr.swift.cube.task.impl;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.structure.Pair;

import java.util.HashMap;

/**
 * @author anchore
 * @date 2017/12/15
 */
public class SchedulerTaskPool extends BaseTaskPool<SchedulerTask> {
    public void initListener() {
        EventDispatcher.listen(TaskEvent.DONE, new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, Pair<TaskKey, TaskResult> result) {
                get(result.getKey()).onDone(result.getValue());
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