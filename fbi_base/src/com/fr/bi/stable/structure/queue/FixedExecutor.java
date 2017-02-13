package com.fr.bi.stable.structure.queue;

import com.fr.bi.base.BICore;
import com.fr.bi.common.inter.Traversal;
import com.fr.stable.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by daniel on 2017/1/10.
 */
public class FixedExecutor<T extends AVTask> {

    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() << 1);
    private Map<BICore, T> tasks = new ConcurrentHashMap<BICore, T>();
    private Map<BICore, BICore> running = new ConcurrentHashMap<BICore, BICore>();
    private QueueThread checkTaskThread = new QueueThread();

    public FixedExecutor() {
        checkTaskThread.setTraversal(new Traversal() {
            @Override
            public void actionPerformed(Object data) {
                synchronized (this) {
                    Iterator<Map.Entry<BICore, T>> iterator = tasks.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<BICore, T> entry = iterator.next();
                        T task = entry.getValue();
                        if (task.isAvailable() && !running.containsKey(entry.getKey())) {
                            running.put(entry.getKey(), entry.getKey());
                            executor.execute(new CallThread(task));
                        }
                    }
                }
            }
        });
        checkTaskThread.start();
    }

    public void  addTask(T task) {
        if(task != null) {
            BICore key = task.getKey();
            synchronized (this) {
                if (!tasks.containsKey(key)) {
                    tasks.put(key, task);
                    triggerCheck();
                }
            }
        }
    }




    private void triggerCheck() {
        if(checkTaskThread.size() < 2) {
            checkTaskThread.add(StringUtils.EMPTY);
        }
    }


    private class CallThread implements Runnable {

        private T task;

        CallThread(T task){
            this.task = task;
        }

        @Override
        public void run()  {
            try {
                if(task != null) {
                    task.work();
                }
                triggerCheck();
            } finally {
                running.remove(task.getKey());
                tasks.remove(task.getKey());
            }
        }
    }

}
