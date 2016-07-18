package com.fr.bi.cal.analyze.executor.detail;

import com.fr.bi.cal.report.io.BIExportUtils;
import com.fr.report.cell.Cell;
import com.fr.report.cellcase.CellCase;
import com.fr.stable.ExportConstants;
import com.fr.web.core.utils.ExportUtils;

import java.util.Iterator;

/**
 * Created by daniel on 2016/7/12.
 */
public class StreamCellCase implements CellCase {

    private DetailCellIterator iterator  = null;

    public StreamCellCase(DetailCellIterator iterator) {
        this.iterator = iterator;
    }
    @Override
    public Cell get(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Cell cell, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cell removeCell(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {

    }

    @Override
    public void insertRow(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeRow(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnCount() {
        return iterator.getColumn();
    }

    @Override
    public int getRowCount() {
        return iterator.getRow();
    }

    @Override
    public Iterator cellIterator() {
        return iterator.getIteratorByPage(0);
    }

    @Override
    public Iterator getColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getColumns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexColumn(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator getRow(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getRows() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexRow(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator intersect(int column_start, int row_start, int column, int row) {
       int page = BIExportUtils.createExcel2007Page(row_start) - 1;
       return iterator.getIteratorByPage(page);
    }

    @Override
    public void recalculate() {
    }

    @Override
    public void toCache(int i, int i1, boolean b) {
    }

    @Override
    public void releaseCache() {
    }


    public Object clone() {
        return this;
    }
}
