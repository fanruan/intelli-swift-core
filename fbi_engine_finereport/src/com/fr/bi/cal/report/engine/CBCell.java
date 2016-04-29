package com.fr.bi.cal.report.engine;

import com.fr.report.cell.AbstractAnalyCellElementFull;
import com.fr.report.cell.AnalyCellElement;
import com.fr.report.cell.ResultCellElement;
import com.fr.report.core.box.BoxElement;


public class CBCell extends AbstractAnalyCellElementFull implements ResultCellElement {

    /**
     *
     */
    private static final long serialVersionUID = 851033565632942475L;
    protected int column = -1;
    protected int row = -1;
    protected int columnSpan = 0;
    protected int rowSpan = 0;


    private CBBoxElement cbbox;

    public CBCell() {
    }

    public CBCell(Object value) {
        this.setValue(value);
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public void setColumn(int col) {
        this.column = col;
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public void setColumnSpan(int colSpan) {
        this.columnSpan = colSpan;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    @Override
    public BoxElement getBoxElement() {
        return cbbox;
    }

    public void setBoxElement(CBBoxElement box) {
        cbbox = box;
    }

    @Override
    public int getSonBoxCESize() {
        return 0;
    }

    @Override
    public AnalyCellElement getSonBoxCE(int index) {
        return null;
    }

}