package com.fr.swift.cube.task;

import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.impl.LocalTaskPool;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;

/**
 * @author anchore
 * @date 2017/12/19
 */
public interface PrevOneDoneHandler {
    /**
     * 前置任务完成时如何处理本任务
     * <p>
     * 可能产生竞争条件：多个前置任务同时做完时，并发调用这个
     *
     * @param selfKey        本任务
     * @param prevDoneOneKey 完成的前置任务
     */
    void handle(TaskKey selfKey, TaskKey prevDoneOneKey);

    /**
     * 默认处理
     * 前置任务失败，取消未执行任务
     * 之后本任务失败
     */
    class DefaultHandler implements PrevOneDoneHandler {
        @Override
        public void handle(TaskKey selfKey, TaskKey prevDoneOneKey) {
            SchedulerTask self = from(selfKey);
            SchedulerTask prevDoneOne = from(prevDoneOneKey);
            synchronized (prevDoneOne) {
                // prevDoneOne不成功，便取消全部未执行的任务
                if (prevDoneOne.result() != Result.SUCCEEDED) {
                    for (TaskKey prevKey : self.prevAll()) {
                        if (prevKey.equals(prevDoneOneKey)) {
                            continue;
                        }
                        SchedulerTask prev = from(prevKey);
                        synchronized (prev) {
                            prev.cancel();
                        }
                    }
                    synchronized (self) {
                        self.cancel();
                    }
                    return;
                }
            }

            // 遍历前置任务，如果全部完成，则启动本任务
            boolean allDone = true;
            for (TaskKey prevKey : self.prevAll()) {
                if (prevKey.equals(prevDoneOneKey)) {
                    continue;
                }
                SchedulerTask prev = from(prevKey);
                synchronized (prev) {
                    if (prev.status() != Status.DONE) {
                        allDone = false;
                        break;
                    }
                }
            }
            if (allDone) {
                self.triggerRun();
            }
        }

        SchedulerTask from(TaskKey key) {
            return SchedulerTaskPool.getInstance().get(key);
        }
    }

    class DefaultLocalHandler extends DefaultHandler {
        @Override
        SchedulerTask from(TaskKey key) {
            return LocalTaskPool.getInstance().get(key);
        }
    }
}