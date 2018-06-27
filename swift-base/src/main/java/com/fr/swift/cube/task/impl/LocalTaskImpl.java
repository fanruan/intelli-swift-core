package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskImpl extends BaseTask implements LocalTask {
    private Worker worker;

    private List<TaskKey> prevTasks = new ArrayList<TaskKey>();
    private List<TaskKey> nextTasks = new ArrayList<TaskKey>();

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

        SwiftLoggers.getLogger().info(String.format("%s %s", key, result));

        LocalTaskTomb.getTomb().add(this);
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

    private static LocalTask from(TaskKey taskKey) {
        return LocalTaskPool.getInstance().get(taskKey);
    }
}