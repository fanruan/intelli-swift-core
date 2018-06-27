package com.fr.swift.cube.task.impl;

import com.fr.event.EventDispatcher;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class SchedulerTaskImpl extends BaseTask implements SchedulerTask {
    private List<TaskKey> prevTasks = new ArrayList<TaskKey>();

    private List<TaskKey> nextTasks = new ArrayList<TaskKey>();

    public SchedulerTaskImpl(TaskKey key) {
        super(key);

        SchedulerTaskPool.getInstance().add(this);
    }

    @Override
    public void cancel() {
        EventDispatcher.fire(TaskEvent.CANCEL, key);
    }

    @Override
    public void triggerRun() {
        synchronized (this) {
            if (status.compare(Status.RUNNABLE) > 0) {
                return;
            }
        }
        setStatus(Status.RUNNABLE);
        EventDispatcher.fire(TaskEvent.TRIGGER, key);
    }

    @Override
    public void onDone(TaskResult result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
        }
        end = System.currentTimeMillis();

        this.result = result;
        setStatus(Status.DONE);

        SwiftLoggers.getLogger().info(String.format("%s %s", key, result));

        SchedulerTaskTomb.getTomb().add(this);
    }


    @Override
    public void addPrev(TaskKey prevKey) {
        if (key.equals(prevKey)) {
            return;
        }
        if (!prevTasks.contains(prevKey)) {
            prevTasks.add(prevKey);
        }
    }

    @Override
    public void addPrev(SchedulerTask prev) {
        addPrev(prev.key());
    }

    @Override
    public void addNext(TaskKey nextKey) {
        if (key.equals(nextKey)) {
            return;
        }
        if (!nextTasks.contains(nextKey)) {
            nextTasks.add(nextKey);
            // 简化操作，顺便把前置也加了
            from(nextKey).addPrev(key);
        }
    }

    @Override
    public void addNext(SchedulerTask next) {
        addNext(next.key());
    }

    @Override
    public List<TaskKey> prevAll() {
        return Collections.unmodifiableList(prevTasks);
    }

    @Override
    public List<TaskKey> nextAll() {
        return Collections.unmodifiableList(nextTasks);
    }

    private static SchedulerTask from(TaskKey taskKey) {
        return SchedulerTaskPool.getInstance().get(taskKey);
    }
}