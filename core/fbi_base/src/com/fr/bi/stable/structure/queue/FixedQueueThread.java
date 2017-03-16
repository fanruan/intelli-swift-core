package com.fr.bi.stable.structure.queue;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.BICore;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.structure.thread.BIThread;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程队列
 * Created by GUY on 2015/3/16.
 */
public class FixedQueueThread<T extends AV>  {

    private static FixedExecutor executor = new FixedExecutor();


    private Traversal<T> back;

    private BrokenTraversal<T> check;

    public FixedQueueThread() {
    }

    public FixedQueueThread(Traversal<T> back) {
        this.back = back;
    }

    public void setCheck(BrokenTraversal<T> check) {
        this.check = check;
    }

    public void setTraversal(Traversal<T> back) {
        this.back = back;
    }

    public void add(final T obj) {
        if(obj == null){
            return;
        }
        executor.addTask(new AVTask() {
            @Override
            public boolean isAvailable() {
                return obj.isAvailable();
            }

            @Override
            public BICore getKey() {
                return obj.getKey();
            }

            @Override
            public void work() {
                if (check != null) {
                    if (!check.actionPerformed(null)){
                        return;
                    }
                }
                if (back != null) {
                    try {
                        back.actionPerformed(obj);
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        });
    }
}