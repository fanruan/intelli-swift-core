package com.fr.bi.cal.generate.queue;

import com.fr.bi.cal.generate.CustomTableTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Lucifer on 2017-4-1.
 * 自定义任务队列
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTaskQueue {

    public BlockingQueue<CustomTableTask> queue = new LinkedBlockingQueue<CustomTableTask>(
            100);

    private static CustomTaskQueue instance;

    static {
        instance = new CustomTaskQueue();
    }

    private CustomTaskQueue() {
    }

    public static CustomTaskQueue getInstance() {
        return instance;
    }

    public void put(CustomTableTask tableTask) throws InterruptedException {
        queue.put(tableTask);
    }

    public synchronized CustomTableTask poll() throws InterruptedException {
        return queue.poll();
    }

    public synchronized CustomTableTask take() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getSize() {
        return queue.size();
    }

    public BlockingQueue<CustomTableTask> getQueue() {
        return queue;
    }
}
