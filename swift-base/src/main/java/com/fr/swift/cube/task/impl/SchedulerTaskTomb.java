package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;

/**
 * @author anchore
 * @date 2018/5/12
 */
class SchedulerTaskTomb extends BaseTaskTomb {
    @Override
    SchedulerTask from(TaskKey key) {
        return SchedulerTaskPool.getInstance().get(key);
    }

    private static final SchedulerTaskTomb TOMB = new SchedulerTaskTomb();

    private SchedulerTaskTomb() {
    }

    static SchedulerTaskTomb getTomb() {
        return TOMB;
    }
}