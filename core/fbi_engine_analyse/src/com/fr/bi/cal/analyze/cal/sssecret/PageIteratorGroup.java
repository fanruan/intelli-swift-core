package com.fr.bi.cal.analyze.cal.sssecret;

public class PageIteratorGroup {
    private static final long serialVersionUID = -5143454742110936232L;

    private NodeDimensionIterator rowIterator;

    private NodeDimensionIterator columnIterator;

    public PageIteratorGroup() {

    }

    public NodeDimensionIterator getRowIterator() {
        return rowIterator;
    }

    public NodeDimensionIterator getColumnIterator() {
        return columnIterator;
    }

    public void setRowIterator(NodeDimensionIterator rowIterator) {
        this.rowIterator = rowIterator;
    }

    public void setColumnIterator(NodeDimensionIterator columnIterator) {
        this.columnIterator = columnIterator;
    }
}