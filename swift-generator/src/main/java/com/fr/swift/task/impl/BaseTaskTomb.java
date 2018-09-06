package com.fr.swift.task.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.Task.Status;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author anchore
 * @date 2018/5/12
 */
abstract class BaseTaskTomb implements Runnable {
    private BlockingQueue<SchedulerTask> tasks = new LinkedBlockingQueue<SchedulerTask>();

    BaseTaskTomb() {
        SwiftExecutors.newThread(this).start();
    }

    public void add(SchedulerTask task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SchedulerTask self = tasks.take();
                for (SchedulerTask next : self.nextAll()) {
                    handle(next, self);
                }
            } catch (InterruptedException ite) {
                SwiftLoggers.getLogger().error(ite);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    private void handle(SchedulerTask self, SchedulerTask prevDoneOne) {
        // prevDoneOne不成功，便取消全部未执行的任务
        if (prevDoneOne.result().getType() != Type.SUCCEEDED) {
            for (SchedulerTask prev : self.prevAll()) {
                if (prev.equals(prevDoneOne)) {
                    continue;
                }
                prev.cancel();
            }
            self.cancel();
            return;
        }

        // 遍历前置任务，如果全部完成，则启动本任务
        boolean allDone = true;
        for (SchedulerTask prev : self.prevAll()) {
            if (prev.equals(prevDoneOne)) {
                continue;
            }
            if (prev.status() != Status.DONE) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            self.triggerRun();
        }
    }
}