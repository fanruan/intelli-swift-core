package com.fr.bi.base;

import com.fr.base.FRContext;

public class BIBusinessPackagePersistThread extends Thread {

    private volatile boolean stop = false;
    private Action running;
    private Action wait;

    public void run() {
        while (!stop) {
            if (wait == null) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        FRContext.getLogger().error(e.getMessage(), e);
                    }
                }
                continue;
            }
            synchronized (this) {
                running = wait;
                wait = null;
            }
            if (running != null) {
                running.work();
            }
        }
    }

    public void triggerWork(Action action) {
        synchronized (this) {
            wait = action;
            this.notify();
        }
    }


    public interface Action {
        void work();
    }

}
