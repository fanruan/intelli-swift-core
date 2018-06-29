package com.fr.swift.event.global;

import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.structure.Pair;

/**
 * @author yee
 * @date 2018/6/29
 */
public class TaskDoneRpcEvent extends AbstractGlobalRpcEvent<Pair<TaskKey, TaskResult>> {
    private static final long serialVersionUID = -3276289536047862494L;

    private Pair<TaskKey, TaskResult> content;

    public TaskDoneRpcEvent(Pair<TaskKey, TaskResult> content) {
        this.content = content;
    }

    @Override
    public Event subEvent() {
        return Event.TASK_DONE;
    }

    @Override
    public Pair<TaskKey, TaskResult> getContent() {
        return content;
    }
}
