package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.structure.Pair;

/**
 * @author anchore
 * @date 2017/12/28
 */
public class WorkerTaskImpl extends BaseTask implements WorkerTask {
    private Worker worker;

    public WorkerTaskImpl(TaskKey key) {
        super(key);
        status = Status.RUNNABLE;
    }

    @Override
    public void onCancel() {
        if (status.order() < Status.RUNNING.order()) {
            done(Result.CANCELLED);
        }
    }

    @Override
    public void run() {
        worker.work();
    }

    @Override
    public void done(final Result result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
            this.result = result;
            setStatus(Status.DONE);
        }

        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<Pair<TaskKey, Result>>() {
                @Override
                public Pair<TaskKey, Result> getContent() {
                    return Pair.of(key, result);
                }

                @Override
                public EventType getEventType() {
                    return EventType.DONE_TASK;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;
        worker.setOwner(this);
    }
}