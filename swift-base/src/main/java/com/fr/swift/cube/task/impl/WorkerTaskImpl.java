package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.cube.task.TaskResult.Type;
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

    public WorkerTaskImpl(TaskKey key, Worker worker) {
        super(key);
        this.worker = worker;
        worker.setOwner(this);
    }

    @Override
    public void onCancel() {
        synchronized (this) {
            if (status.order() >= Status.RUNNING.order()) {
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
    public void done(final TaskResult result) {
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
        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<Pair<TaskKey, TaskResult>>() {
                @Override
                public Pair<TaskKey, TaskResult> getContent() {
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
}