package com.fr.swift.cube.task.impl;

import com.fr.event.Event;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.structure.Pair;

import java.util.Map;

/**
 * @author anchore
 * @date 2018/6/26
 */
public class TaskEvent<T> implements Event<T> {
    public static final Event<TaskKey> TRIGGER = new TaskEvent<TaskKey>();

    public static final Event<Map<TaskKey, ?>> RUN = new TaskEvent<Map<TaskKey, ?>>();

    public static final Event<Map<TaskKey, ?>> LOCAL_RUN = new TaskEvent<Map<TaskKey, ?>>();

    public static final Event<TaskKey> CANCEL = new TaskEvent<TaskKey>();

    public static final Event<Pair<TaskKey, TaskResult>> DONE = new TaskEvent<Pair<TaskKey, TaskResult>>();
    public static final Event<Pair<TaskKey, TaskResult>> LOCAL_DONE = new TaskEvent<Pair<TaskKey, TaskResult>>();

    private TaskEvent() {
    }
}