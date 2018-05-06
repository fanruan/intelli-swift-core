package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;

/**
 * @author anchore
 * @date 2018/5/4
 */
public class DefaultLocalHandler extends DefaultHandler {
    @Override
    SchedulerTask from(TaskKey key) {
        return LocalTaskPool.getInstance().get(key);
    }
}