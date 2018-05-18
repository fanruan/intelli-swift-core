package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.SingleThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author anchore
 * @date 2018/5/12
 */
abstract class BaseTaskTomb implements Runnable {
    private BlockingQueue<SchedulerTask> tasks = new LinkedBlockingQueue<SchedulerTask>();

    BaseTaskTomb() {
        new SingleThreadFactory(getClass().getSimpleName()).newThread(this).start();
    }

    public void add(SchedulerTask task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        while (true) {
            try {
                SchedulerTask self = tasks.take();
                for (TaskKey next : self.nextAll()) {
                    handle(next, self.key());
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    private void handle(TaskKey selfKey, TaskKey prevDoneOneKey) {
        SchedulerTask self = from(selfKey);
        SchedulerTask prevDoneOne = from(prevDoneOneKey);
        // prevDoneOne不成功，便取消全部未执行的任务
        if (prevDoneOne.result().getType() != Type.SUCCEEDED) {
            for (TaskKey prevKey : self.prevAll()) {
                if (prevKey.equals(prevDoneOneKey)) {
                    continue;
                }
                SchedulerTask prev = from(prevKey);
                prev.cancel();
            }
            self.cancel();
            return;
        }

        // 遍历前置任务，如果全部完成，则启动本任务
        boolean allDone = true;
        for (TaskKey prevKey : self.prevAll()) {
            if (prevKey.equals(prevDoneOneKey)) {
                continue;
            }
            SchedulerTask prev = from(prevKey);
            if (prev.status() != Status.DONE) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            self.triggerRun();
        }
    }

    abstract SchedulerTask from(TaskKey key);
}