package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.PrevOneDoneHandler;
import com.fr.swift.cube.task.PrevOneDoneHandler.DefaultLocalHandler;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskImpl extends BaseTask implements LocalTask {
    private Worker worker;
    private final PrevOneDoneHandler prevOneDoneHandler;

    private List<TaskKey> prevTasks = new ArrayList<TaskKey>();
    private List<TaskKey> nextTasks = new ArrayList<TaskKey>();

    public LocalTaskImpl(TaskKey key) {
        this(key, new DefaultLocalHandler());
    }

    public LocalTaskImpl(final TaskKey key, PrevOneDoneHandler prevOneDoneHandler) {
        super(key);
        this.prevOneDoneHandler = prevOneDoneHandler;

        LocalTaskPool.getInstance().add(this);
    }

    @Override
    public void onPrevOneDone(TaskKey prevDoneOne) {
        synchronized (prevOneDoneHandler) {
            prevOneDoneHandler.handle(key, prevDoneOne);
        }
    }

    @Override
    public void cancel() {
        onCancel();
    }

    @Override
    public void onCancel() {
        if (status.order() < Status.RUNNING.order()) {
            done(Result.CANCELLED);
        }
    }

    @Override
    public void triggerRun() {
        setStatus(Status.RUNNABLE);
        CubeTaskManager.getInstance().run(this);
    }

    @Override
    public void run() {
        worker.work();
    }

    @Override
    public void done(final Result result) {
        onDone(result);
    }

    @Override
    public void onDone(Result result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
            this.result = result;
            setStatus(Status.DONE);
        }

        SwiftLoggers.getLogger().info(String.format("%s %s", key, result));

        for (TaskKey next : nextTasks) {
            from(next).onPrevOneDone(key);
        }
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
        return prevTasks;
    }

    @Override
    public List<TaskKey> nextAll() {
        return nextTasks;
    }

    private static LocalTask from(TaskKey taskKey) {
        return LocalTaskPool.getInstance().get(taskKey);
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;
        worker.setOwner(this);
    }
}