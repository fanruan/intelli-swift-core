package com.fr.bi.report.result.imp;

import com.fr.bi.report.result.BIDetailCell;
import com.fr.bi.report.result.BIDetailTableResult;

import java.util.Iterator;
import java.util.List;

/**
 * Created by andrew_asa on 2017/8/3.
 */
public class DetailTableResult implements BIDetailTableResult {

    Iterator<List<BIDetailCell>> iterator;

    /**
     * 行数
     */
    private int rowSize;

    /**
     * 列数
     */
    private int columnSize;

    public DetailTableResult(List<List<BIDetailCell>> result) {

        if (result != null && result.size() > 0) {
            setIterator(result.iterator());
            rowSize = result.size();
            columnSize = result.get(0).size();
        }
    }

    @Override
    public boolean hasNext() {

        if (iterator != null) {
            return iterator.hasNext();
        }
        return false;
    }

    @Override
    public List<BIDetailCell> next() {

        return iterator.next();
    }

    @Override
    public void remove() {

        iterator.remove();
    }

    public void setIterator(Iterator<List<BIDetailCell>> iterator) {

        this.iterator = iterator;
    }

    public Iterator<List<BIDetailCell>> getIterator() {

        return iterator;
    }

    @Override
    public int rowSize() {

        return rowSize;
    }

    @Override
    public int columnSize() {

        return columnSize;
    }

    public void setColSize(int rowSize) {

        this.rowSize = rowSize;
    }

    public void setColumnSize(int columnSize) {

        this.columnSize = columnSize;
    }
}
