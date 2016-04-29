package com.fr.bi.stable.structure.queue;

import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.structure.thread.BIThread;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程队列
 * Created by GUY on 2015/3/16.
 */
public class QueueThread<T> implements BIQueue<T>, BIThread {

    private final QThread qThread = new QThread();

    private Queue<T> generatorQueue = new LinkedBlockingQueue<T>();

    private T generated;

    private T generating;

    private Traversal<T> back;

    private BrokenTraversal<T> check;

    public QueueThread() {

    }

    public QueueThread(Traversal<T> back) {
        this.back = back;
    }

    public void setCheck(BrokenTraversal<T> check) {
        this.check = check;
    }

    public void setTraversal(Traversal<T> back) {
        this.back = back;
    }

    public T getGenerated() {
        return generated;
    }

    public void setGenerated(T generated) {
        this.generated = generated;
    }

    public T getGenerating() {
        return generating;
    }

    public void setGenerating(T generating) {
        this.generating = generating;
    }

    @Override
    public Iterator<T> iterator() {
        return generatorQueue.iterator();
    }

    @Override
    public boolean contains(T obj) {
        return generatorQueue.contains(obj);
    }

    @Override
    public void remove(T obj) {
        generatorQueue.remove(obj);
    }

    @Override
    public boolean add(T obj) {
        generatorQueue.add(obj);
        synchronized (qThread) {
            qThread.notifyAll();
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return generating == null && generatorQueue.isEmpty();
    }

    @Override
    public int size() {
        return generatorQueue.size();
    }

    @Override
    public T peek() {
        return generatorQueue.peek();
    }

    @Override
    public T poll() {
        return generatorQueue.poll();
    }

    @Override
    public void clear() {
        generatorQueue.clear();
        interrupt();
    }

    @Override
    public void start() {
        qThread.start();
    }

    @Override
    public void notifyThread() {
        synchronized (qThread) {
            qThread.notifyAll();
        }
    }

    @Override
    public void interrupt() {
        qThread.stop = true;
        synchronized (qThread) {
            qThread.interrupt();
        }
    }

    private class QThread extends Thread {

        private volatile boolean stop = false;

        private QThread() {
        }

        @Override
        public void run() {
            p:
            while (!stop) {
                if (generatorQueue.size() == 0) {
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                if (check != null) {
                    while (!check.actionPerformed(null)) {
                        synchronized (this) {
                            try {
                                //TODO 需要check error 信息
                                this.wait();
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (check.actionPerformed(null)) {
                            continue p;
                        }
                    }
                }

                generating = generatorQueue.peek();

                if (back != null) {
                    try {
                        back.actionPerformed(generating);
                    } catch (Exception e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }

                generatorQueue.poll();
                generated = generating;
                generating = null;
            }
        }
    }
}