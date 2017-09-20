package com.fr.bi.cal.report.io.iterator;

import com.finebi.cube.common.log.BILoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by daniel on 2016/7/12.
 */
public class TableCellIterator implements Iterator {

    private volatile boolean isEnd = false;
    private int column;
    private int row;
    private volatile List<StreamPagedIterator> iters;
    private volatile int pageIndex = 0;

    public TableCellIterator(int column, int row) {
        this.column = column;
        this.row = row;
        this.iters = new ArrayList<StreamPagedIterator>();
        iters.add(new StreamPagedIterator());
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public StreamPagedIterator getIteratorByPage(int page) {
        if (iters.size() > page) {
            return iters.get(page);
        }

        StreamPagedIterator pagedIterator = new StreamPagedIterator();
        iters.add(pagedIterator);

        return pagedIterator;
    }

    public void finishOnePage(int idx) {
        synchronized (this) {
            iters.get(idx).finish();
            iters.add(new StreamPagedIterator());
            this.notify();
        }
    }

    public void finish() {
        for (StreamPagedIterator iter : iters) {
            iter.finish();
        }
        isEnd = true;
        synchronized (this) {
            this.notify();
        }
    }

    private void waitFor() {
        synchronized (this) {
            while (!isEnd && (pageIndex > 0 && !iters.get(pageIndex - 1).isEnd())) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        waitFor();
        return pageIndex < iters.size();
    }

    @Override
    public StreamPagedIterator next() {
        synchronized (iters) {
            return iters.get(pageIndex++);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
