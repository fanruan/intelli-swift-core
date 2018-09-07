package com.fr.swift.task.impl;

import com.fr.swift.task.TaskResult;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.task.WorkerTask.Worker;

/**
 * @author anchore
 * @date 2018/1/19
 */
public abstract class BaseWorker implements Worker {
    private WorkerTask owner;

    public static Worker nullWorker() {
        return new BaseWorker() {
            @Override
            public void work() {
                // do nothing
                workOver(new TaskResultImpl(Type.SUCCEEDED));
            }
        };
    }

    @Override
    public final void setOwner(WorkerTask owner) {
        this.owner = owner;
    }

    @Override
    public final void workOver(TaskResult result) {
        if (owner != null) {
            owner.done(result);
        }
    }
}