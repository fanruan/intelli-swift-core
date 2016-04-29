package com.fr.bi.cal.report.engine;

import com.fr.report.cell.Cell;
import com.fr.report.cell.CellElement;
import com.fr.report.cellcase.CellCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CBCellCase implements CellCase {
    /**
     *
     */
    private static final long serialVersionUID = -7574464284920882220L;
    private CBCell[][] cbcells = null;

    public CBCellCase(CBCell[][] gecells) {
        this.cbcells = gecells;
    }

    @Override
    public Cell get(int col, int row) {
        return this.cbcells[col][row];
    }

    @Override
    public void add(Cell ce, boolean override) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cell removeCell(int column, int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertRow(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeRow(int rowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertColumn(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeColumn(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnCount() {
        return cbcells.length;
    }

    @Override
    public int getRowCount() {
        return cbcells[0].length;
    }

    @Override
    public Iterator cellIterator() {
        return new CellIterator();
    }

    @Override
    public Iterator getColumn(int columnIndex) {
        return new ColumnIterator(columnIndex);
    }

    @Override
    public int[] getColumns() {
        return new int[0];
    }

    @Override
    public int getIndexColumn(int columnIndex) {
        return 0;
    }

    @Override
    public Iterator getRow(int rowIndex) {
        return new RowIterator(rowIndex);
    }

    @Override
    public int[] getRows() {
        return new int[0];
    }

    @Override
    public int getIndexRow(int rowIndex) {
        return 0;
    }

    @Override
    public Iterator intersect(int column, int row, int width, int height) {
        return new IntersectIterator(column, row, width, height);
    }

    @Override
    public void recalculate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toCache(int maxCount, int activePoolCount, boolean cacheFirst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseCache() {
        throw new UnsupportedOperationException();
    }

    //clone
    @Override
    public Object clone() throws CloneNotSupportedException {
        CBCellCase newcase = (CBCellCase) super.clone();

        return newcase;
    }

    private class CellIterator implements Iterator {
        int thisrow = -1;
        int thiscol = -1;
        int nextrow = 0;
        int nextcol = -1;

        CellElement next = null;

        public CellIterator() {
            findNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Object next() {
            if (!hasNext())
                throw new NoSuchElementException("At last element");
            CellElement ce = next;
            thiscol = nextcol;
            thisrow = nextrow;
            findNext();
            return ce;
        }

        @Override
        public void remove() {
            removeCell(thiscol, thisrow);
        }

        protected void findNext() {
            int c = nextcol + 1;
            for (int r = nextrow; r < cbcells.length; r++) {
                for (; c < cbcells[r].length; c++) {
                    CBCell ce = cbcells[r][c];
                    if (ce != null && ce.getColumn() == r && ce.getRow() == c) {
                        nextrow = r;
                        nextcol = c;

                        next = ce;
                        return;
                    }
                }
                c = 0;
            }

            next = null;
        }
    }

    private class ColumnIterator implements Iterator {
        private int columnIndex;

        private int current_row = -1;
        private int next_row = 0;
        private CellElement next;

        ColumnIterator(int columnIndex) {
            this.columnIndex = columnIndex;
            findNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Object next() {
            if (!hasNext())
                throw new NoSuchElementException("At last element");
            CellElement ce = next;
            findNext();
            return ce;
        }

        @Override
        public void remove() {
            removeCell(columnIndex, current_row);
        }

        private void findNext() {
            while (next_row < cbcells[columnIndex].length) {
                CBCell ce = cbcells[columnIndex][next_row];

                current_row = next_row;
                next_row++;

                if (ce != null && ce.getColumn() == columnIndex && ce.getRow() == current_row) {
                    next = ce;
                    return;
                }
            }

            next = null;
        }

    }

    private class RowIterator implements Iterator {
        int current_column = -1;
        int next_column = 0;
        CellElement next;
        private int rowIndex;

        public RowIterator(int rowIndex) {
            this.rowIndex = rowIndex;
            findNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Object next() {
            if (!hasNext())
                throw new NoSuchElementException("At last element");
            CellElement ce = next;
            findNext();
            return ce;
        }

        @Override
        public void remove() {
            removeCell(current_column, rowIndex);
        }

        private void findNext() {
            if (rowIndex < 0 || rowIndex >= cbcells[0].length) {
                next = null;
                return;
            }
            while (next_column < cbcells.length) {
                CBCell ce = cbcells[next_column][rowIndex];

                current_column = next_column;
                next_column++;

                if (ce != null && ce.getColumn() == current_column && ce.getRow() == rowIndex) {
                    next = ce;
                    return;
                }
            }

            next = null;
        }
    }

    //b:范围区域的cell的iterator
    private class IntersectIterator implements Iterator {
        int thisrow = -1;
        int thiscol = -1;
        int nextrow = 0;
        int nextcol = -1;
        CellElement next = null;
        private int columnFrom;
        private int columnTo;
        private int rowFrom;
        private int rowTo;

        private IntersectIterator(int column, int row, int width, int height) {
            columnFrom = Math.max(column, 0);
            columnTo = Math.min(column + width - 1, getColumnCount() - 1);
            rowFrom = Math.max(row, 0);
            rowTo = Math.min(row + height - 1, getRowCount() - 1);

            this.nextrow = rowFrom;
            this.nextcol = columnFrom - 1;

            findNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Object next() {
            if (!hasNext())
                throw new NoSuchElementException("At last element");
            CellElement ce = next;
            thiscol = nextcol;
            thisrow = nextrow;
            findNext();
            return ce;
        }

        @Override
        public void remove() {
            removeCell(thiscol, thisrow);
        }

        protected void findNext() {
            int c = nextcol + 1;
            for (int r = nextrow; r <= rowTo; r++) {
                if (cbcells[0].length <= r) {
                    break;
                }
                for (; c <= columnTo; c++) {
                    CBCell ce = cbcells[c][r];

                    if (ce == null) {
                        continue;
                    }

                    int tx1 = Math.max(ce.getColumn(), columnFrom);
                    int ty1 = Math.max(ce.getRow(), rowFrom);
                    int tx2 = Math.min(ce.getColumn() + ce.getColumnSpan(), columnTo + 1);
                    int ty2 = Math.min(ce.getRow() + ce.getRowSpan(), rowTo + 1);

                    // tx1 == c && ty1 == r 表示交叉区域为左上角的点位
                    if (tx2 > tx1 && ty2 > ty1 && tx1 == c && ty1 == r) {
                        nextrow = r;
                        nextcol = c;

                        next = ce;
                        return;
                    }
                }
                c = columnFrom;
            }

            next = null;
        }
    }
}