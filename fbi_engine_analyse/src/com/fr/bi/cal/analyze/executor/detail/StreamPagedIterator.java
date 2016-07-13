package com.fr.bi.cal.analyze.executor.detail;

import com.fr.report.cell.CellElement;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by daniel on 2016/7/13.
 */
public class StreamPagedIterator implements Iterator<CellElement> {

    private final static int MAX_CELLS = 1 << 14;
    private final static int MIN_CELLS = MAX_CELLS >> 1;
    private volatile Queue<CellElement> queue = new LinkedList<CellElement>();
    private volatile boolean isEnd = false;


    private void waitFor() {
        if(queue.size() < MIN_CELLS) {
            synchronized (this) {
                this.notify();
            }
        }
        if(queue.isEmpty()) {
            synchronized (this) {
                synchronized (queue) {
                    if (queue.isEmpty() && (!isEnd)) {
                        try {
                            this.wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean hasNext() {
        waitFor();
        return (!isEnd) || (!queue.isEmpty());
    }

    @Override
    public CellElement next() {
        synchronized (queue) {
            try {
                return queue.poll();
            } catch (Exception e) {
                return null;
            } finally {
                if(queue.isEmpty()){
                    synchronized (this) {
                        this.notify();
                    }
                }
            }

        }
    }

    public void finish() {
        isEnd = true;
        synchronized (this) {
            this.notify();
        }
    }

    public void addCell(CellElement cellElement) {
        if(queue.size() > MAX_CELLS) {
            synchronized (this) {
                if(queue.size() > MAX_CELLS) {
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
            this.notify();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
