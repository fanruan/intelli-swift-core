package com.fr.swift.task.impl;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.WorkerTask;

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
        SwiftEventDispatcher.fire(TaskEvent.LOCAL_DONE, Pair.of(key, result));
    }
}