package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.WorkerTask.Worker;

/**
 * @author anchore
 * @date 2018/1/19
 */
public abstract class BaseWorker implements Worker {
    private WorkerTask owner;

    @Override
    public final void setOwner(WorkerTask owner) {
        this.owner = owner;
    }

    @Override
    public final void workOver(Result result) {
        if (owner != null) {
            owner.done(result);
        }
    }

    public static Worker nullWorker() {
        return new BaseWorker() {
            @Override
            public void work() {
                // do nothing
                workOver(Result.SUCCEEDED);
            }
        };
    }
}