package com.fr.swift.task.cube;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.task.Task.Status;
import com.fr.swift.task.TaskExecutor;
import com.fr.swift.task.TaskStatusChangeListener;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import static com.fr.swift.task.Task.Status.DONE;
import static com.fr.swift.task.Task.Status.RUNNING;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeTaskExecutor implements TaskExecutor {
    private BlockingQueue<WorkerTask> tasks = new LinkedBlockingQueue<WorkerTask>();
    private Poller poller;

    CubeTaskExecutor(String name, int threadNum) {
        poller = new Poller(name, threadNum);
        SwiftExecutors.newThread(poller, name + "-Poller").start();
    }

    @Override
    public void add(WorkerTask task) {
        task.addStatusChangeListener(poller.listener);
        tasks.add(task);
    }

    private class Poller implements Runnable {
        Executor exec;
        Semaphore ticket;
        TaskStatusChangeListener listener = new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (prev == RUNNING && now == DONE) {
                    ticket.release();
                }
            }
        };

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
    }
}