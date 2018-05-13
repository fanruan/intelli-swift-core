package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;

/**
 * @author anchore
 * @date 2018/5/12
 */
class LocalTaskTomb extends BaseTaskTomb {
    @Override
    SchedulerTask from(TaskKey key) {
        return LocalTaskPool.getInstance().get(key);
    }

    private static final LocalTaskTomb TOMB = new LocalTaskTomb();

    private LocalTaskTomb() {
    }

    static LocalTaskTomb getTomb() {
        return TOMB;
    }
}