package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskExecutor;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import static com.fr.swift.cube.task.Task.Status.DONE;
import static com.fr.swift.cube.task.Task.Status.RUNNING;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeTaskExecutor implements TaskExecutor {
    private BlockingQueue<WorkerTask> runnableTasks = new LinkedBlockingQueue<WorkerTask>();
    private Poller poller;

    CubeTaskExecutor(String name, int threadNum) {
        poller = new Poller(threadNum);
        new Thread(poller, name).start();
    }

    @Override
    public void add(WorkerTask task) {
        task.addStatusChangeListener(poller.listener);
        runnableTasks.add(task);
    }

    private class Poller implements Runnable {
        Executor exec;
        Semaphore ranSemaphore;

        Poller(int threadNum) {
            exec = Executors.newFixedThreadPool(threadNum);
            ranSemaphore = new Semaphore(threadNum);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ranSemaphore.acquire();
                    WorkerTask task = runnableTasks.take();
                    synchronized (task) {
                        if (task.status() == Status.RUNNABLE) {
                            task.setStatus(Status.RUNNING);
                            exec.execute(task);
                        }
                    }
                }
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        TaskStatusChangeListener listener = new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (prev == RUNNING && now == DONE) {
                    ranSemaphore.release();
                }
            }
        };
    }
}