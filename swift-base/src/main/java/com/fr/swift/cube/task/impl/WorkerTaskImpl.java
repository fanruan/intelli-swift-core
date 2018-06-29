package com.fr.swift.cube.task.impl;

import com.fr.event.EventDispatcher;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.structure.Pair;

/**
 * @author anchore
 * @date 2017/12/28
 */
public class WorkerTaskImpl extends BaseTask implements WorkerTask {
    private Worker worker;

    public WorkerTaskImpl(TaskKey key) {
        this(key, BaseWorker.nullWorker());
    }

    public WorkerTaskImpl(TaskKey key, Worker worker) {
        super(key);
        this.worker = worker;
        worker.setOwner(this);
    }

    @Override
    public void onCancel() {
        synchronized (this) {
            if (status.compare(Status.RUNNING) > 0) {
                return;
            }
        }
        done(new TaskResultImpl(Type.CANCELLED));
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();

        worker.work();

        end = System.currentTimeMillis();
    }

    @Override
    public void done(TaskResult result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
        }
        this.result = result;
        setStatus(Status.DONE);

        triggerDoneEvent(result);
    }

    private void triggerDoneEvent(final TaskResult result) {
        EventDispatcher.fire(TaskEvent.LOCAL_DONE, Pair.of(key, result));
    }
}