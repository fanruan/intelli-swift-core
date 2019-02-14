package com.fr.swift.task.impl;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class SchedulerTaskImpl extends BaseTask implements SchedulerTask {
    protected List<SchedulerTask> prevTasks = new ArrayList<SchedulerTask>();

    protected List<SchedulerTask> nextTasks = new ArrayList<SchedulerTask>();

    public SchedulerTaskImpl(TaskKey key) {
        super(key);

        SchedulerTaskPool.getInstance().add(this);
    }

    @Override
    public void cancel() {
        SwiftEventDispatcher.fire(TaskEvent.CANCEL, key);
    }

    @Override
    public void triggerRun() {
        synchronized (this) {
            if (status.compare(Status.RUNNABLE) > 0) {
                return;
            }
        }
        setStatus(Status.RUNNABLE);
        SwiftEventDispatcher.fire(TaskEvent.TRIGGER, key);
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

        TaskTomb.getTomb().add(this);
    }

    @Override
    public void addPrev(SchedulerTask prev) {
        if (equals(prev)) {
            return;
        }
        if (!prevTasks.contains(prev)) {
            prevTasks.add(prev);
        }
    }

    @Override
    public void addNext(SchedulerTask next) {
        if (equals(next)) {
            return;
        }
        if (!nextTasks.contains(next)) {
            nextTasks.add(next);
            // 简化操作，顺便把前置也加了
            next.addPrev(this);
        }
    }

    @Override
    public List<SchedulerTask> prevAll() {
        return prevTasks;
    }

    @Override
    public List<SchedulerTask> nextAll() {
        return nextTasks;
    }
}