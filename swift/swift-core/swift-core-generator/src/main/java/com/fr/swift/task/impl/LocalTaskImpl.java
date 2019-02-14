package com.fr.swift.task.impl;

import com.fr.swift.task.LocalTask;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.cube.CubeTaskManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskImpl extends BaseTask implements LocalTask {
    private List<SchedulerTask> prevTasks = new ArrayList<SchedulerTask>();

    private List<SchedulerTask> nextTasks = new ArrayList<SchedulerTask>();

    private Worker worker;

    public LocalTaskImpl(TaskKey key) {
        this(key, BaseWorker.nullWorker());
    }

    public LocalTaskImpl(TaskKey key, Worker worker) {
        super(key);
        this.worker = worker;
        worker.setOwner(this);

        LocalTaskPool.getInstance().add(this);
    }

    @Override
    public void cancel() {
        onCancel();
    }

    @Override
    public void onCancel() {
        synchronized (this) {
            if (status.compare(Status.RUNNING) >= 0) {
                return;
            }
        }
        done(new TaskResultImpl(Type.CANCELLED));
    }

    @Override
    public void triggerRun() {
        CubeTaskManager.getInstance().run(this);
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();

        worker.work();

        end = System.currentTimeMillis();
    }

    @Override
    public void done(TaskResult result) {
        onDone(result);
    }

    @Override
    public void onDone(TaskResult result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
        }
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