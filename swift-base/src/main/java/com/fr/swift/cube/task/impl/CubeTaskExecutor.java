package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskExecutor;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.thread.SwiftExecutors;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SingleThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import static com.fr.swift.cube.task.Task.Status.DONE;
import static com.fr.swift.cube.task.Task.Status.RUNNING;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeTaskExecutor implements TaskExecutor {
    private BlockingQueue<WorkerTask> tasks = new LinkedBlockingQueue<WorkerTask>();
    private Poller poller;

    CubeTaskExecutor(String name, int threadNum) {
        poller = new Poller(name, threadNum);
        new SingleThreadFactory(name + "-Poller").newThread(poller).start();
    }

    @Override
    public void add(WorkerTask task) {
        task.addStatusChangeListener(poller.listener);
        tasks.add(task);
    }

    private class Poller implements Runnable {
        Executor exec;
        Semaphore ticket;

        Poller(String name, int threadNum) {
            exec = SwiftExecutors.newFixedThreadPool(threadNum, new PoolThreadFactory(name));
            ticket = new Semaphore(threadNum);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ticket.acquire();
                    WorkerTask task = tasks.take();
                    if (task.status() == Status.RUNNABLE) {
                        task.setStatus(RUNNING);
                        exec.execute(task);
                    } else {
                        ticket.release();
                    }
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().error(e);
                    Thread.currentThread().interrupt();
                }
            }

        }

        TaskStatusChangeListener listener = new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (prev == RUNNING && now == DONE) {
                    ticket.release();
                }
            }
        };
    }
}