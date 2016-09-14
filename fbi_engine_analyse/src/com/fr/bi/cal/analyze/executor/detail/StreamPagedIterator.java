package com.fr.bi.cal.analyze.executor.detail;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by daniel on 2016/7/13.
 */
public class StreamPagedIterator<T> implements Iterator<T> {

    private int maxCount = 1 << 14;
    private int halfCount = maxCount >> 1;
    private volatile Queue<T> queue = new LinkedList<T>();
    private volatile boolean isEnd = false;

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        this.halfCount = maxCount >> 1;
    }

    private void waitFor() {
        if(queue.size() == halfCount) {
            synchronized (this) {
                this.notify();
            }
        }
        if(queue.isEmpty()) {
            synchronized (this) {
                while (isRealEmpty() && (!isEnd)) {
                    try {
                        this.wait();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private boolean isRealEmpty() {
        synchronized (queue){
            return queue.isEmpty();
        }
    }


    @Override
    public boolean hasNext() {
        waitFor();
        return (!isEnd) || (!isRealEmpty());
    }

    @Override
    public T next() {
        synchronized (queue) {
            return queue.poll();
        }
    }

    public void finish() {
        isEnd = true;
        synchronized (this) {
            this.notify();
        }
    }

    public void addCell(T cellElement) {
        if(queue.size() > maxCount) {
            synchronized (this) {
                if(queue.size() > maxCount) {
                    try {
                        this.wait();
                    } catch (Exception e) {
                    }
                }
            }
        }
        synchronized (queue) {
            queue.add(cellElement);
        }
        synchronized (this) {
            if (queue.size() > (maxCount - 1)){
                this.notify();
            }
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
