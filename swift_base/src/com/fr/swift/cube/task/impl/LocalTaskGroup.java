package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.WorkerTask;

/**
 * @author anchore
 * @date 2018/1/12
 *
 * 将小任务组成任务组，组内任务都在本机，通知可以快些，稳定些
 * @see LocalTask
 */
public class LocalTaskGroup extends BaseTask implements WorkerTask {
    private LocalTask start;

    public LocalTaskGroup(TaskKey key) {
        super(key);
    }

    @Override
    public void onCancel() {
        start.cancel();
    }

    @Override
    public void done(final Result result) {
        this.result = result;
        setStatus(Status.DONE);
    }

    @Override
    public void setWorker(Worker worker) {
        // no need for worker
    }

    @Override
    public void run() {
        setStatus(Status.RUNNING);
        start.triggerRun();
    }

    public void wrap(LocalTask start, final LocalTask end) {
        this.start = start;
        end.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (now == Status.DONE) {
                    done(end.result());
                }
            }
        });
    }
}